package org.example.client.texture;

import java.util.HashMap;
import java.util.Map;

public class SpriteManager {

    private final Map<String, SpriteSheet> sheets = new HashMap<>();

    public SpriteSheet getSheet(String resourcePath) {

        // Return the existing sheet if already loaded
        if (sheets.containsKey(resourcePath)) {
            return sheets.get(resourcePath);
        }

        // Otherwise load it once and cache it
        SpriteSheet sheet = new SpriteSheet(resourcePath);

        sheets.put(resourcePath, sheet);

        return sheet;
    }

    public void close() {
        for (SpriteSheet sheet : sheets.values()) {
            sheet.close();
        }

        sheets.clear();
    }
}