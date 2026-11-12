package org.example.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class GameSettings {

    private float musicVolume = 0.5f;
    private float soundVolume = 0.8f;

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setMusicVolume(float volume) {
        if (!Float.isFinite(volume)) {
            return;
        }

        this.musicVolume = Math.max(
                0.0f, Math.min(1.0f, volume)
        );
    }

    public void setSoundVolume(float volume) {
        if (!Float.isFinite(volume)) {
            return;
        }

        this.soundVolume = Math.max(
                0.0f, Math.min(1.0f, volume)
        );
    }

    public void load() {
        Path path = GamePaths.getSettingsPath();

        if (!Files.exists(path)) {
            return; // Keep default values
        }

        Properties properties = new Properties();

        try (InputStream input = Files.newInputStream(path)) {
            properties.load(input);

            setMusicVolume(
                    readVolume(properties, "musicVolume", musicVolume)
            );

            setSoundVolume(
                    readVolume(properties, "soundVolume", soundVolume)
            );

        } catch (IOException e) {
            System.err.println(
                    "Failed to load game settings: " + e.getMessage()
            );
        }
    }

    private float readVolume(
            Properties properties,
            String key,
            float defaultValue
    ) {
        String value = properties.getProperty(key);

        if (value == null) {
            return defaultValue;
        }

        try {
            float parsed = Float.parseFloat(value);

            return Float.isFinite(parsed)
                    ? parsed
                    : defaultValue;

        } catch (NumberFormatException e) {
            System.err.println(
                    "Invalid setting for " + key + ": " + value
            );

            return defaultValue;
        }
    }

    public void save() {
        Path path = GamePaths.getSettingsPath();

        Properties properties = new Properties();

        properties.setProperty(
                "musicVolume", Float.toString(musicVolume)
        );

        properties.setProperty(
                "soundVolume", Float.toString(soundVolume)
        );

        try {
            Files.createDirectories(path.getParent());

            try (OutputStream output = Files.newOutputStream(path)) {
                properties.store(output, "Hero of the Draw Settings");
            }

        } catch (IOException e) {
            System.err.println(
                    "Failed to save game settings: " + e.getMessage()
            );
        }
    }
}