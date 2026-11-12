package org.example.client;

import java.nio.file.Path;

public final class GamePaths {

    private static final String GAME_FOLDER = "HeroOfTheDraw";

    private GamePaths() {
        // Utility class - no instances needed
    }

    public static Path getDataDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");

            if (appData != null && !appData.isBlank()) {
                return Path.of(appData, GAME_FOLDER);
            }

            return Path.of(home, "AppData", "Roaming", GAME_FOLDER);
        }

        if (os.contains("mac")) {
            return Path.of(
                    home, "Library", "Application Support", GAME_FOLDER
            );
        }

        String xdgData = System.getenv("XDG_DATA_HOME");

        if (xdgData != null && !xdgData.isBlank()) {
            return Path.of(xdgData, GAME_FOLDER);
        }

        return Path.of(home, ".local", "share", GAME_FOLDER);
    }

    public static Path getSettingsPath() {
        return getDataDirectory().resolve("settings.properties");
    }

    public static Path getSavesDirectory() {
        return getDataDirectory().resolve("saves");
    }
}