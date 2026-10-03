package org.example.client.texture;

import org.example.serializer.Codec;
import org.example.serializer.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import com.raylib.Raylib;

public record UploadedTexture(Raylib.Texture texture, TextureType type, @Nullable TextureMetadata metadata) {
    public static final Codec<TextureMetadata> CODEC = TextureMetadata.CODEC.fieldOf("gui");

    public enum TextureType {
        NONE,
        NINE_SLICE
    }

    public record TextureMetadata(TextureType type, int width, int height, int scale, int corner) {
        public static final Codec<TextureMetadata> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                    Codec.ofEnum(TextureType.class).optionalFieldOf("type", TextureType.NONE).forGetter(TextureMetadata::type),
                    Codec.INT.fieldOf("width").forGetter(TextureMetadata::width),
                    Codec.INT.fieldOf("height").forGetter(TextureMetadata::height),
                    Codec.INT.fieldOf("scale").forGetter(TextureMetadata::scale),
                    Codec.INT.fieldOf("corner").forGetter(TextureMetadata::corner)
                ).apply(TextureMetadata::new)
        );
    }
}
