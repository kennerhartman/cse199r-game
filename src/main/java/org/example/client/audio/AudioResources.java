package org.example.client.audio;

import java.io.IOException;
import java.io.InputStream;

// AI generated

/**
 * Reads audio files packed on the classpath (works the same from the IDE and
 * from the jar), so raylib can decode them from memory instead of a file path.
 */
final class AudioResources {
    private AudioResources() {
    }

    /**
     * Returns the bytes of the classpath resource at {@code resourcePath}
     * (e.g. {@code "/music/stargazer_na.mp3"}), or null if it does not exist.
     */
    static byte[] read(String resourcePath) {
        try (InputStream input = AudioResources.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                return null;
            }

            return input.readAllBytes();

        } catch (IOException e) {
            System.err.println(
                    "[Audio] Failed to read " + resourcePath + ": " + e.getMessage()
            );
            return null;
        }
    }

    /** raylib picks its decoder from this, e.g. {@code ".wav"} or {@code ".mp3"}. */
    static String fileType(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(dot).toLowerCase() : "";
    }
}
