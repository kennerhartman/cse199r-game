package org.example.client.audio;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import com.raylib.Raylib;

// AI generated
public class SoundManager {
    public enum SoundType {
        BUTTON_CLICK("button_click.wav"),
        CARD_DRAW("card_draw.wav"),
        CARD_PLAY("card_play.wav"),
        ATTACK("attack.wav"),
        PURCHASE("purchase.wav"),
        NPC_SPEECH("npc_speech.wav");

        private final String fileName;

        SoundType(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }

    /** How many copies of one sound may play at the same time. */
    private static final int VOICES_PER_SOUND = 4;

    /** Pitch spread used by {@link #playVaried(SoundType)} (+/- 5%). */
    private static final float PITCH_VARIATION = 0.05f;

    /** Index 0 is the loaded sound, the rest are aliases sharing its data. */
    private final Map<SoundType, Raylib.Sound[]> voices =
            new EnumMap<>(SoundType.class);

    private final Map<SoundType, Integer> nextVoice =
            new EnumMap<>(SoundType.class);

    private final Random random = new Random();

    private float volume = 0.8f;

    public SoundManager() {
        for (SoundType type : SoundType.values()) {
            load(type, type.getFileName());
        }
    }

    /**
     * Loads (or replaces) the sound for {@code type} from
     * {@code /sound_effects/<resourceFileName>}. Logs a warning and leaves the
     * sound silent if the file is missing or invalid.
     */
    public void load(SoundType type, String resourceFileName) {
        byte[] bytes = AudioResources.read("/sound_effects/" + resourceFileName);

        if (bytes == null) {
            System.err.println(
                    "[SoundManager] Missing sound effect: /sound_effects/"
                            + resourceFileName + " (" + type + " will be silent)"
            );
            return;
        }

        // Decode from memory; the Sound gets its own copy of the samples,
        // so the Wave and the byte[] can be discarded afterwards.
        Raylib.Wave wave = Raylib.LoadWaveFromMemory(
                AudioResources.fileType(resourceFileName), bytes, bytes.length
        );

        Raylib.Sound base = null;

        if (Raylib.IsWaveValid(wave)) {
            base = Raylib.LoadSoundFromWave(wave);
            Raylib.UnloadWave(wave);
        }

        if (base == null || !Raylib.IsSoundValid(base)) {
            System.err.println(
                    "[SoundManager] Could not decode sound effect: "
                            + resourceFileName + " (" + type + " will be silent)"
            );
            return;
        }

        unload(type);

        Raylib.Sound[] pool = new Raylib.Sound[VOICES_PER_SOUND];
        pool[0] = base;

        for (int i = 1; i < pool.length; i++) {
            pool[i] = Raylib.LoadSoundAlias(base);
        }

        voices.put(type, pool);
        nextVoice.put(type, 0);
    }

    /** Plays the sound at the global SFX volume and normal pitch. */
    public void play(SoundType type) {
        play(type, 1.0f, 1.0f);
    }

    /**
     * Plays the sound with its volume scaled by {@code volumeScale}
     * (0.0 - 1.0, multiplied with the global SFX volume).
     */
    public void play(SoundType type, float volumeScale) {
        play(type, volumeScale, 1.0f);
    }

    /**
     * Plays the sound with a volume scale and pitch
     * (1.0 = normal, 2.0 = an octave up, 0.5 = an octave down).
     */
    public void play(SoundType type, float volumeScale, float pitch) {
        Raylib.Sound voice = acquireVoice(type);

        if (voice == null) {
            return;
        }

        float scale = Math.max(0.0f, Math.min(1.0f, volumeScale));

        Raylib.SetSoundVolume(voice, volume * scale);
        Raylib.SetSoundPitch(voice, Math.max(0.01f, pitch));
        Raylib.PlaySound(voice);
    }

    /**
     * Plays the sound with a small random pitch change so repeated effects
     * (hits, card draws) don't sound identical.
     */
    public void playVaried(SoundType type) {
        float pitch = 1.0f
                + (random.nextFloat() * 2.0f - 1.0f) * PITCH_VARIATION;

        play(type, 1.0f, pitch);
    }

    /** Stops every playing copy of this sound. */
    public void stop(SoundType type) {
        Raylib.Sound[] pool = voices.get(type);

        if (pool == null) {
            return;
        }

        for (Raylib.Sound voice : pool) {
            Raylib.StopSound(voice);
        }
    }

    public boolean isLoaded(SoundType type) {
        return voices.containsKey(type);
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public float getVolume() {
        return volume;
    }

    public void close() {
        for (SoundType type : SoundType.values()) {
            unload(type);
        }
    }

    /** Picks a voice that isn't playing, or the oldest one if all are busy. */
    private Raylib.Sound acquireVoice(SoundType type) {
        Raylib.Sound[] pool = voices.get(type);

        if (pool == null) {
            return null;
        }

        int start = nextVoice.getOrDefault(type, 0);

        for (int i = 0; i < pool.length; i++) {
            int index = (start + i) % pool.length;

            if (!Raylib.IsSoundPlaying(pool[index])) {
                nextVoice.put(type, (index + 1) % pool.length);
                return pool[index];
            }
        }

        // All voices busy: steal the next one in round-robin order.
        nextVoice.put(type, (start + 1) % pool.length);
        Raylib.StopSound(pool[start]);
        return pool[start];
    }

    private void unload(SoundType type) {
        Raylib.Sound[] pool = voices.remove(type);
        nextVoice.remove(type);

        if (pool == null) {
            return;
        }

        // Aliases must be unloaded before the sound that owns the data.
        for (int i = pool.length - 1; i >= 1; i--) {
            Raylib.UnloadSoundAlias(pool[i]);
        }

        Raylib.UnloadSound(pool[0]);
    }
}
