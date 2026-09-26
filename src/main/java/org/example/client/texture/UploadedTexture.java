package org.example.client.texture;

import com.raylib.Raylib;

// TODO: IMPLEMENT
public class UploadedTexture {
    Raylib.Texture texture;
    TextureType type;

    public static class TextureMetaData {

    }

    enum TextureTypes {
        NINE_SLICE
    }

    record TextureType(TextureTypes type, int width, int height, int cornerSize) {}
}
