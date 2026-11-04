package org.example.GameLoop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.Character.GameSaveData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveSystem {
//    TODO: Refactor to work with saving all game data not just character eventually
    public static void saveGame(GameSaveData game, int saveNumber){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        Chose save file based on save slot
//        TODO: Handle correct saving based on OS LATER when we actually have the game ready to download
        File saveFile = new File("./saves/save"+saveNumber+".json");

//        Checks for parent directory and creates it if necessary
        File parentDir = saveFile.getParentFile();
        if(parentDir !=null && !parentDir.exists()){
            parentDir.mkdirs();
        }

//        Writes save to file
        try(FileWriter writer = new FileWriter(saveFile)){
            gson.toJson(game, writer);
            System.out.println("Game Saved Successfully");
        } catch(IOException e){
            System.err.println("Error Saving Game: " + e.getMessage());
        }
    }

    public static boolean doesSaveExist(String saveName) {
        File saveFile = new File("./saves/" + saveName + ".json");

        return saveFile.exists();
    }
}
