package org.example.client.audio;

import java.util.EnumMap;
import java.util.Map;

import com.raylib.Raylib;

public class SoundManager {

    public enum SoundType {
        BUTTON_CLICK,
        CARD_DRAW,
        CARD_PLAY,
        ATTACK,
        PURCHASE,
        NPC_SPEACH
    }

    private final Map<SoundType, Raylib.Sound> sounds =
            new EnumMap<>(SoundType.class);

    private float volume = 0.8f;

    public void load(SoundType type, String path) {
        Raylib.Sound sound = Raylib.LoadSound(path);

        if (!Raylib.IsSoundValid(sound)) {
            throw new IllegalStateException(
                    "Failed to load sound: " + path
            );
        }

        Raylib.SetSoundVolume(sound, volume);
        sounds.put(type, sound);
    }

    public void play(SoundType type) {
        Raylib.Sound sound = sounds.get(type);

        if (sound == null) {
            return;
        }

        Raylib.PlaySound(sound);
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));

        for (Raylib.Sound sound : sounds.values()) {
            Raylib.SetSoundVolume(sound, this.volume);
        }
    }

    private String getSoundPath(String filename) {
        try {
            var resource = SoundManager.class.getResourceAsStream(
                    "/sound_effects/" + filename
            );

            if (resource == null) {
                throw new IllegalArgumentException(
                        "Sound resource not found: " + filename
                );
            }

            java.nio.file.Path tempFile =
                    java.nio.file.Files.createTempFile("sound_", ".wav");

            tempFile.toFile().deleteOnExit();

            try (resource) {
                java.nio.file.Files.copy(
                        resource,
                        tempFile,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
            }

            return tempFile.toAbsolutePath().toString();

        } catch (java.io.IOException e) {
            throw new RuntimeException(
                    "Failed to load sound: " + filename, e
            );
        }
    }

    public float getVolume() {
        return volume;
    }

    public void close() {
        for (Raylib.Sound sound : sounds.values()) {
            Raylib.UnloadSound(sound);
        }

        sounds.clear();
    }
}