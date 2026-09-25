package org.example.client.texture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.util.Identifier;

import com.raylib.Raylib;

public class TextureAtlas {
    public Map<Identifier, Raylib.Texture> textures = new HashMap<>();

    private final Identifier location;

    public TextureAtlas(Identifier location) {
        this.location = location;
    }

    public void prepare() {
        List<Identifier> textureLocations = TextureLoader.prepare(this.location);

        this.upload(textureLocations);
    }

    public void upload(List<Identifier> textureLocations) {
        Map<Identifier, Raylib.Texture> textures = TextureLoader.upload(textureLocations);
        this.textures.putAll(textures);
    }

    public Raylib.Texture getTexture(Identifier textureId) {
        return this.textures.get(textureId);
    }
}
