package org.example.client.audio;

import java.util.EnumMap;
import java.util.Map;

import java.util.Random;

import org.bytedeco.javacpp.BytePointer;

import com.raylib.Raylib;

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

    /** Encoded file bytes each stream reads from; freed in close(). */
    private final Map<MusicType, BytePointer> trackData =
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

    /** Requires the audio device to be initialised (see {@code Client}). */
    public MusicManager() {
        load(MusicType.MENU, "stargazer_na.mp3");
        load(MusicType.SHOP_1, "a_shop_that_doesnt_exist_na.mp3");
        load(MusicType.SHOP_2, "lets_groove_na.mp3");
        load(MusicType.MAP, "falling_leaves_prelude_DbM_na.mp3");
        load(MusicType.COMBAT_1, "catastrophic_impact_na.mp3");
        load(MusicType.COMBAT_2, "prayer_in_a_land_of_war_na.mp3");
    }

    private void load(MusicType type, String fileName) {
        byte[] bytes = AudioResources.read("/music/" + fileName);

        if (bytes == null) {
            throw new IllegalArgumentException(
                    "Music resource not found: " + fileName
            );
        }

        // raylib streams from this buffer while the track plays (it does not
        // copy it), so it must live in native memory until the track is
        // unloaded. A plain byte[] would only be pinned for the call itself.
        BytePointer data = new BytePointer(bytes.length);
        data.put(bytes);

        Raylib.Music music = Raylib.LoadMusicStreamFromMemory(
                AudioResources.fileType(fileName), data, bytes.length
        );

        if (!Raylib.IsMusicValid(music)) {
            data.close();
            throw new IllegalStateException(
                    "Failed to load music: " + fileName
            );
        }

        Raylib.SetMusicVolume(music, volume);
        tracks.put(type, music);
        trackData.put(type, data);
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

        // Free the encoded audio only after its stream has been unloaded.
        for (BytePointer data : trackData.values()) {
            data.close();
        }

        trackData.clear();
        currentMusic = null;
        currentType = null;
    }
}