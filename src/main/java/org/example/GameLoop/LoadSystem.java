package org.example.GameLoop;

import com.google.gson.Gson;
import org.example.Character.GameSaveData;
import org.example.Character.PlayerCharacter;
import org.example.Player.Player;
import org.example.card.CardStorage;
import org.example.client.GamePaths;
import org.example.map.MapManager;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoadSystem {
    private static final Gson gson = new Gson();

    public static GameSaveData loadGame(int saveNumber) {
        Path saveDirectory = GamePaths.getSavesDirectory();

        Path savePath = saveDirectory.resolve(
                "save" + saveNumber + ".json"
        );

        Path backupPath = saveDirectory.resolve(
                "save" + saveNumber + ".json.bak"
        );

        // Try the main save first
        GameSaveData gameSave = loadFromPath(savePath);

        if (gameSave != null) {
            System.out.println("Game Loaded Successfully");
            return gameSave;
        }

        // If the main save failed, try the backup
        System.err.println(
                "Primary save unavailable. Checking backup..."
        );

        gameSave = loadFromPath(backupPath);

        if (gameSave != null) {
            System.out.println(
                    "Game Recovered Successfully From Backup"
            );
        } else {
            System.err.println(
                    "No valid save or backup found for slot "
                            + saveNumber
            );
        }

        return gameSave;
    }

    private static GameSaveData loadFromPath(Path path) {
        if (!Files.isRegularFile(path)) {
            return null;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            GameSaveData data = gson.fromJson(
                    reader,
                    GameSaveData.class
            );

            return data;

        } catch (IOException | com.google.gson.JsonParseException e) {
            System.err.println(
                    "Failed to load " + path.getFileName()
                            + ": " + e.getMessage()
            );

            return null;
        }
    }

    public static PlayerCharacter loadCharacter(CardStorage cards, GameSaveData gameSave, Player player){
//        Creates a new character object based on the game save and returns it
        PlayerCharacter savedCharacter = new PlayerCharacter(
                player,
                gameSave.name,
                gameSave.level,
                gameSave.Health,
                gameSave.Strength,
                gameSave.Speed,
                gameSave.Attack,
                gameSave.currency,
                gameSave.spriteId != null
                        ? gameSave.spriteId
                        : "knight");
        for(String cardName : gameSave.deck){
            savedCharacter.deck.drawPile.add(cards.findCard(cardName));
        }
//        Debugging v
        System.out.println("Loaded: "+savedCharacter.name+"'s game belonging to "+player.username);
        return savedCharacter;
    }

    public static MapManager loadMap(GameSaveData gameSave){
        MapManager savedMap = new MapManager(gameSave.allNodes);

        return savedMap;
    }
}
