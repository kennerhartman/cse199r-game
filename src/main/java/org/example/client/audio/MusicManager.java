package org.example.client.audio;

import java.util.EnumMap;
import java.util.Map;

import com.raylib.Raylib;
import java.util.Random;

public class MusicManager {

    public enum MusicType {
        MENU,
        SHOP_1,
        SHOP_2,
        MAP,
        COMBAT_1,
        COMBAT_2
    }

    private final Map<MusicType, Raylib.Music> tracks =
            new EnumMap<>(MusicType.class);

    private MusicType currentType = null;
    private Raylib.Music currentMusic = null;

    private float volume = 0.5f;

    private enum FadeState {
        NONE,
        FADING_OUT,
        FADING_IN
    }

    private FadeState fadeState = FadeState.NONE;

    private MusicType nextType = null;

    private float fadeProgress = 0.0f;
    private float fadeDuration = 1.0f;
    private float fadeStartVolume = 1.0f;

    private final Random random = new Random();

    public MusicManager() {
        Raylib.InitAudioDevice();

        load(MusicType.MENU, getMusicPath("stargazer_na.mp3"));
        load(MusicType.SHOP_1, getMusicPath("a_shop_that_doesnt_exist_na.mp3"));
        load(MusicType.SHOP_2, getMusicPath("lets_groove_na.mp3"));
        load(MusicType.MAP, getMusicPath("falling_leaves_prelude_DbM_na.mp3"));
        load(MusicType.COMBAT_1, getMusicPath("catastrophic_impact_na.mp3"));
        load(MusicType.COMBAT_2, getMusicPath("prayer_in_a_land_of_war_na.mp3"));
    }

    private void load(MusicType type, String path) {
        Raylib.Music music = Raylib.LoadMusicStream(path);

        if (!Raylib.IsMusicValid(music)) {
            throw new IllegalStateException(
                    "Failed to load music: " + path
            );
        }

        Raylib.SetMusicVolume(music, volume);
        tracks.put(type, music);
    }

    public void play(MusicType type) {
        if (type == null) {
            return;
        }

        // Ignore duplicate requests for the same destination
        if (type == (nextType != null ? nextType : currentType)) {
            return;
        }

        // If no music is playing, start fading in
        if (currentMusic == null) {
            nextType = type;
            switchTrack();

            fadeProgress = 0.0f;
            fadeState = FadeState.FADING_IN;
            return;
        }

        // If we're already fading out, keep fading
        if (fadeState == FadeState.FADING_OUT) {
            if (type == currentType) {
                // The player returned to the current track.
                // Fade it back in instead of switching.
                nextType = null;
                fadeStartVolume = 1.0f - fadeProgress;
                fadeProgress = 0.0f;
                fadeState = FadeState.FADING_IN;
            } else {
                // Change the destination without restarting the fade
                nextType = type;
            }
            return;
        }

        // Start fading out from the current audible level
        if (fadeState == FadeState.FADING_IN) {
            fadeStartVolume = fadeProgress;
        } else {
            fadeStartVolume = 1.0f;
        }

        nextType = type;
        fadeProgress = 0.0f;
        fadeState = FadeState.FADING_OUT;
    }

    private void switchTrack() {
        if (currentMusic != null) {
            Raylib.StopMusicStream(currentMusic);
        }

        currentType = nextType;
        currentMusic = tracks.get(currentType);
        nextType = null;

        if (currentMusic != null) {
            Raylib.SetMusicVolume(currentMusic, 0.0f);
            Raylib.PlayMusicStream(currentMusic);
        }
    }

    private void playRandom(MusicType first, MusicType second) {
        MusicType selected = random.nextBoolean()
                ? first
                : second;

        play(selected);
    }

    public void playRandomShopMusic() {
        playRandom(MusicType.SHOP_1, MusicType.SHOP_2);
    }

    public void playRandomCombatMusic() {
        playRandom(MusicType.COMBAT_1, MusicType.COMBAT_2);
    }

    public void update() {
        float deltaTime = Raylib.GetFrameTime();

        if (currentMusic != null) {
            Raylib.UpdateMusicStream(currentMusic);
        }

        if (fadeState == FadeState.NONE) {
            return;
        }

        fadeProgress += deltaTime / fadeDuration;
        fadeProgress = Math.min(fadeProgress, 1.0f);

        if (fadeState == FadeState.FADING_OUT) {

            float multiplier =
                    fadeStartVolume * (1.0f - fadeProgress);

            Raylib.SetMusicVolume(
                    currentMusic,
                    volume * multiplier
            );

            if (fadeProgress >= 1.0f) {
                switchTrack();

                fadeStartVolume = 0.0f;
                fadeProgress = 0.0f;
                fadeState = FadeState.FADING_IN;
            }

        } else if (fadeState == FadeState.FADING_IN) {

            float multiplier =
                    fadeStartVolume +
                            (1.0f - fadeStartVolume) * fadeProgress;

            Raylib.SetMusicVolume(
                    currentMusic,
                    volume * multiplier
            );

            if (fadeProgress >= 1.0f) {
                fadeState = FadeState.NONE;
                fadeStartVolume = 1.0f;
            }
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));

        if (currentMusic == null) {
            return;
        }

        float multiplier = switch (fadeState) {
            case FADING_OUT ->
                    fadeStartVolume * (1.0f - fadeProgress);

            case FADING_IN ->
                    fadeStartVolume +
                            (1.0f - fadeStartVolume) * fadeProgress;

            case NONE -> 1.0f;
        };

        Raylib.SetMusicVolume(
                currentMusic,
                this.volume * multiplier
        );
    }

    public void close() {
        if (currentMusic != null) {
            Raylib.StopMusicStream(currentMusic);
        }

        for (Raylib.Music music : tracks.values()) {
            Raylib.UnloadMusicStream(music);
        }

        tracks.clear();
        currentMusic = null;
        currentType = null;

        Raylib.CloseAudioDevice();
    }

    private String getMusicPath(String filename) {
        try {
            var resource = MusicManager.class.getResourceAsStream(
                    "/music/" + filename
            );

            if (resource == null) {
                throw new IllegalArgumentException(
                        "Music resource not found: " + filename
                );
            }

            java.nio.file.Path tempFile =
                    java.nio.file.Files.createTempFile("music_", ".mp3");

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
                    "Failed to load music: " + filename, e
            );
        }
    }
}