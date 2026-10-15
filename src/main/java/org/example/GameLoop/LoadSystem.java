package org.example.GameLoop;

import com.google.gson.Gson;
import org.example.Character.GameSaveData;
import org.example.Character.PlayerCharacter;
import org.example.Player.Player;
import org.example.card.CardStorage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LoadSystem {
//    TODO: Refactor to work with GameSaving rather than just CharacterSaving
    public static GameSaveData loadGame(int saveNumber){
//        Get the file based on the save slot
        File saveFile = new File("./saves/save"+saveNumber+".json");
//        TODO: add real error logging here
//        Checks if the file exists, error if not
        if(!saveFile.exists()){
            System.out.println("No save file found");
            return null;
        }
        Gson gson = new Gson();
//        Creates new GameSaveData based on the save file, or else throws an error
        try(FileReader reader = new FileReader(saveFile)){
            GameSaveData gameSave = gson.fromJson(reader, GameSaveData.class);

            System.out.println("Game Loaded Successfully");

            return gameSave;
        } catch(IOException e){
            System.err.println("Error Loading Game: " + e.getMessage());
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
                gameSave.currency);
        for(String cardName : gameSave.deck){
            savedCharacter.deck.drawPile.add(cards.findCard(cardName));
        }
//        Debugging v
        System.out.println("Loaded: "+savedCharacter.name+"'s game belonging to "+player.username);
        return savedCharacter;
    }
}
