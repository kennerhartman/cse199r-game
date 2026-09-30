package org.example.client.texture;

import org.jetbrains.annotations.Nullable;

import com.raylib.Raylib;

public class UploadedTexture {
    public Raylib.Texture texture;
    public TextureType type;
    public @Nullable TextureMetadata metadata;

    public UploadedTexture(Raylib.Texture texture, TextureType type, @Nullable TextureMetadata metadata) {
        this.texture = texture;
        this.type = type;
        this.metadata = metadata;
    }

    public enum TextureType {
        NONE,
        NINE_SLICE
    }

    public record TextureMetadata(TextureType type, int width, int height, int scale, int cornerSize) {}
}
