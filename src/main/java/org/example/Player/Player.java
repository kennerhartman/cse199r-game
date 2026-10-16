package org.example.Player;

import org.example.Character.PlayerCharacter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class Player {
//    TODO: Finish Player implementation
    String username;
    String password;
    @Nullable PlayerCharacter character;
    public ArrayList<PlayerCharacter> AllPlayerCharacters = new ArrayList<PlayerCharacter>();
    int PlayerLevel;
    int HighestLevelReached;
    int TotalPlaythroughs;

    public void CreateNewCharacter(String name){
        this.character = new PlayerCharacter(this, name);
        AllPlayerCharacters.add(this.character);
    }

    public void CreateCustomCharacter(String name, int level, int health, int strength, int speed, int attack){
        this.character = new PlayerCharacter(this, name, level, health, strength, speed, attack);
        AllPlayerCharacters.add(this.character);
    }

    public void LoadPrevCharacter(String name, int level){
        for(PlayerCharacter character : AllPlayerCharacters){
            if(Objects.equals(character.name, name) & character.level == level){
                this.character = character;
            }else{
                System.out.println("Character information not found");
            }
        }
    }

    public @Nullable PlayerCharacter getCharacter() {
        return this.character;
    }
}
