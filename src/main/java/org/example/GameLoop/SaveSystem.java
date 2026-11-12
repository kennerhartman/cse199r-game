package org.example.GameLoop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.Character.GameSaveData;
import org.example.client.GamePaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;

public class SaveSystem {

    private static final Gson gson =
            new GsonBuilder().setPrettyPrinting().create();

    public static void saveGame(GameSaveData game, int saveNumber) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Path saveDirectory = GamePaths.getSavesDirectory();

        Path savePath = saveDirectory.resolve(
                "save" + saveNumber + ".json"
        );

        Path backupPath = saveDirectory.resolve(
                "save" + saveNumber + ".json.bak"
        );

        Path tempPath = null;

        try {
            Files.createDirectories(saveDirectory);

            // 1. Create temporary file in the same directory
            tempPath = Files.createTempFile(
                    saveDirectory,
                    "save" + saveNumber + "_",
                    ".tmp"
            );

            // 2. Write the new save data
            try (Writer writer = Files.newBufferedWriter(tempPath)) {
                gson.toJson(game, writer);
            }

            // 3. Back up the previous save
            if (Files.isRegularFile(savePath) && isValidSave(savePath)) {
                Files.copy(
                        savePath,
                        backupPath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            // 4. Atomically replace the original
            try {
                Files.move(
                        tempPath,
                        savePath,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (AtomicMoveNotSupportedException e) {
                // Fallback for filesystems without atomic moves
                Files.move(
                        tempPath,
                        savePath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            System.out.println("Game Saved Successfully");

        } catch (IOException e) {
            System.err.println(
                    "Error Saving Game: " + e.getMessage()
            );
        } finally {
            // Remove temporary file if it still exists
            if (tempPath != null) {
                try {
                    Files.deleteIfExists(tempPath);
                } catch (IOException e) {
                    System.err.println(
                            "Could not clean up temporary save: "
                                    + e.getMessage()
                    );
                }
            }
        }
    }

    public static boolean doesSaveExist(String saveName) {
        Path savePath = GamePaths.getSavesDirectory()
                .resolve(saveName + ".json");

        return Files.isRegularFile(savePath);
    }

    private static boolean isValidSave(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            GameSaveData data = gson.fromJson(
                    reader,
                    GameSaveData.class
            );

            return data != null;

        } catch (IOException | com.google.gson.JsonParseException e) {
            return false;
        }
    }
}