package org.example.map;

import java.util.List;
import java.util.Optional;

import org.example.core.serializer.Codec;
import org.example.core.serializer.RecordCodecBuilder;

public class Map {
    public List<Space> spaces;

    public static final Codec<Map> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Space.CODEC.listOf().fieldOf("spaces").forGetter(Map::getSpaces)
        ).apply(Map::new)
    );

    public Map(List<Space> spaces) {
        this.spaces = spaces;
    }

    public List<Space> getSpaces() {
        return this.spaces;
    }

    public Optional<Space> getSpaceFromMap(MapNode node) {
        if (node == null) {
            return Optional.empty();
        }

        for (Space space : this.spaces) {
            if (space.step() == node.getNodesSinceBoss() && space.type() == node.getType()) {
                return Optional.of(space);
            }
        }

        return Optional.empty();
    }

    public record Space(MapNode.NodeType type, int x, int y, int step) {
        public static final Codec<Space> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.ofEnum(MapNode.NodeType.class).fieldOf("type").forGetter(Space::type),
                        Codec.INT.fieldOf("x").forGetter(Space::x),
                        Codec.INT.fieldOf("y").forGetter(Space::y),
                        Codec.INT.fieldOf("step").forGetter(Space::step)
                ).apply(Space::new)
        );
    }
}
