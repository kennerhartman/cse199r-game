package org.example.client.map.renderer;

import org.example.client.gui.Gui;
import org.example.client.gui.GuiGraphics;
import org.example.client.map.MapInputHandler;
import org.example.client.map.MapView;
import org.example.map.Map;
import org.example.map.MapNode;
import org.example.util.Identifier;

import com.raylib.Colors;
import com.raylib.Raylib;

public class MapRenderer {
    private static final int SPACE_SIZE = MapView.SPACE_SIZE;
    private static final int FRAME_BORDER = 64;

    private static final Raylib.Color LOCKED_TINT = color(110, 110, 110, 255);
    private static final Raylib.Color VISITED_TINT = color(200, 200, 200, 255);
    private static final Raylib.Color TOOLTIP_BACKGROUND = color(0, 0, 0, 180);

    private final MapView view;
    private final MapCamera camera;
    private final MapInputHandler input;

    public MapRenderer(MapView view, MapCamera camera, MapInputHandler input) {
        this.view = view;
        this.camera = camera;
        this.input = input;
    }

    public void render(GuiGraphics graphics, double mouseX, double mouseY) {
        MapView.SpaceStates states = this.view.computeStates();
        Identifier mapId = this.view.getMapId();

        Raylib.BeginScissorMode(0, 0, Gui.VIRTUAL_WIDTH, Gui.VIRTUAL_HEIGHT);
        Raylib.BeginMode2D(this.camera.raw());

        this.renderFrame(graphics);

        graphics.blit(
                Identifier.of(mapId.namespace, "pieces/" + mapId.path + ".png"),
                0,
                0,
                MapView.MAP_WIDTH,
                MapView.MAP_HEIGHT
        );

        this.renderSpaces(graphics, states, this.camera.toWorld(mouseX, mouseY));

        Raylib.EndMode2D();
        Raylib.EndScissorMode();
    }

    private void renderFrame(GuiGraphics graphics) {
        int frameWidth = MapView.MAP_WIDTH + FRAME_BORDER;
        int frameHeight = MapView.MAP_HEIGHT + FRAME_BORDER;

        graphics.blitNineSliced(
                GuiGraphics.TexturePipeline.MAP,
                Identifier.ofDefault("map_background.png"),
                -FRAME_BORDER / 2,
                -FRAME_BORDER / 2,
                frameWidth,
                frameHeight,
                64,
                64
        );
    }

    private void renderSpaces(GuiGraphics graphics, MapView.SpaceStates states, Raylib.Vector2 worldMouse) {
        Map.Space hoveredSpace = null;

        for (Map.Space space : this.view.getMap().getSpaces()) {
            boolean hovered = states.isAvailable(space) && MapView.contains(space, worldMouse);

            if (hovered) {
                hoveredSpace = space;
            }

            String texture = space.type().name().toLowerCase() + (hovered && this.input.isPressed() ? "_pressed" : "");

            graphics.blit(
                    Identifier.of(this.view.getMapId().namespace, "space/" + texture + ".png"),
                    space.x(),
                    space.y(),
                    SPACE_SIZE,
                    SPACE_SIZE,
                    tintFor(space, states)
            );
        }

        if (hoveredSpace != null) {
            this.renderTooltip(graphics, hoveredSpace, states.available().get(hoveredSpace));
        }
    }

    private static Raylib.Color tintFor(Map.Space space, MapView.SpaceStates states) {
        if (states.isAvailable(space) || states.isCurrent(space)) {
            return Colors.WHITE;
        }

        return states.isVisited(space) ? VISITED_TINT : LOCKED_TINT;
    }

    private void renderTooltip(GuiGraphics graphics, Map.Space space, MapNode choice) {
        String tooltip = choice.getTooltip();
        int textWidth = GuiGraphics.getTextWidth(tooltip);
        int textHeight = GuiGraphics.getTextHeight(tooltip);
        int textX = space.x() + SPACE_SIZE / 2 - textWidth / 2;
        int textY = space.y() - textHeight - 6;

        graphics.rectangle(textX - 4, textY - 2, textWidth + 8, textHeight + 4, TOOLTIP_BACKGROUND);
        graphics.text(tooltip, textX, textY, Colors.WHITE);
    }

    private static Raylib.Color color(int r, int g, int b, int a) {
        return new Raylib.Color().r((byte) r).g((byte) g).b((byte) b).a((byte) a);
    }
}
