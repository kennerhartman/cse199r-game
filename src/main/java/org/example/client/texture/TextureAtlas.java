package org.example.client.texture;

import java.util.HashMap;
import java.util.Map;

import org.example.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class TextureAtlas {
    public Map<Identifier, UploadedTexture> textures = new HashMap<>();

    public void prepare() {
        Map<Identifier, UploadedTexture.@Nullable TextureMetadata> textureLocations = TextureLoader.prepare();

        this.upload(textureLocations);
    }

    public void upload(Map<Identifier, UploadedTexture.@Nullable TextureMetadata> textureLocations) {
        Map<Identifier, UploadedTexture> textures = TextureLoader.upload(textureLocations);
        this.textures.putAll(textures);
    }

    public UploadedTexture getTexture(Identifier textureId) {
        return this.textures.get(textureId);
    }
}
