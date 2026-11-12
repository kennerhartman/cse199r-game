package org.example.client.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.example.core.registries.Registries;
import org.example.map.Map;
import org.example.map.MapManager;
import org.example.map.MapNode;
import org.example.util.Identifier;

import com.raylib.Raylib;

public class MapView {
    public static final int SPACE_SIZE = 32;
    public static final int MAP_WIDTH = 724;
    public static final int MAP_HEIGHT = 357;

    private static final Identifier FALLBACK_MAP_ID = Identifier.ofDefault("map_tile_1");
    private static final int LAST_FIXED_TILE = 6;

    private final Random random = new Random();
    private final java.util.Map<Integer, Integer> randomLayouts = new HashMap<>();

    private Map map;
    private final MapManager mapManager;

    private Identifier mapId;
    private int loadedTile = Integer.MIN_VALUE;

    public MapView(MapManager mapManager) {
        this.mapManager = mapManager;
        this.sync();
    }

    public Map getMap() {
        return this.map;
    }

    public Identifier getMapId() {
        return this.mapId;
    }

    public void sync() {
        int tile = this.displayTile();

        if (tile == this.loadedTile) {
            return;
        }

        int layout = this.layoutFor(tile);

        Identifier id;

        if (layout == 0) {
            id = Identifier.ofDefault("tavern");
        } else {
            id = Identifier.ofDefault("map_tile_" + layout);
        }

        Map loaded = Registries.MAPS.getValue(id);

        if (loaded == null) {
            System.out.println("No map data for " + id + ", falling back to " + FALLBACK_MAP_ID);

            id = FALLBACK_MAP_ID;
            loaded = Registries.MAPS.getValue(id);
        }

        this.mapId = id;
        this.map = loaded;
        this.loadedTile = tile;
    }

    private int displayTile() {
        List<MapNode> choices = this.mapManager.getActiveChoices();

        if (choices.isEmpty()) {
            return this.mapManager.getCurrentNode().getTile();
        }

        return choices.getFirst().getTile();
    }

    private int layoutFor(int tile) {
        if (tile <= LAST_FIXED_TILE) {
            return tile;
        }

        return this.randomLayouts.computeIfAbsent(tile, i -> this.random.nextInt(LAST_FIXED_TILE) + 1);
    }

    public SpaceStates computeStates() {
        this.sync();

        MapNode currentNode = this.mapManager.getCurrentNode();
        int tile = this.loadedTile;

        Map.Space current = currentNode.getTile() != tile
                ? null
                : this.map.getSpaceFromMap(currentNode).orElse(null);

        Set<Map.Space> visited = new HashSet<>();

        for (MapNode node : this.mapManager.getPath()) {
            if (node.getTile() == tile) {
                this.map.getSpaceFromMap(node).ifPresent(visited::add);
            }
        }

        java.util.Map<Map.Space, MapNode> available = new HashMap<>();

        for (MapNode choice : this.mapManager.getActiveChoices()) {
            this.map.getSpaceFromMap(choice).ifPresent(space -> available.put(space, choice));
        }

        return new SpaceStates(current, visited, available);
    }

    public Optional<MapNode> choiceAt(Raylib.Vector2 worldPos) {
        for (java.util.Map.Entry<Map.Space, MapNode> entry : this.computeStates().available().entrySet()) {
            if (contains(entry.getKey(), worldPos)) {
                return Optional.of(entry.getValue());
            }
        }

        return Optional.empty();
    }

    public void select(MapNode choice) {
        this.mapManager.selectNode(choice);
    }

    public static boolean contains(Map.Space space, Raylib.Vector2 worldPos) {
        return worldPos.x() > space.x() && worldPos.x() < space.x() + SPACE_SIZE
                && worldPos.y() > space.y() && worldPos.y() < space.y() + SPACE_SIZE;
    }

    public record SpaceStates(
            Map.Space current,
            Set<Map.Space> visited,
            java.util.Map<Map.Space, MapNode> available
    ) {
        public boolean isAvailable(Map.Space space) {
            return this.available.containsKey(space);
        }

        public boolean isCurrent(Map.Space space) {
            return space.equals(this.current);
        }

        public boolean isVisited(Map.Space space) {
            return this.visited.contains(space);
        }
    }
}
