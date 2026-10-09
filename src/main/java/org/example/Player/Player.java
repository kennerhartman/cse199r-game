package org.example.Player;

import org.example.Character.PlayerCharacter;

import java.util.ArrayList;
import java.util.Objects;

public class Player {
//    TODO: Finish Player implementation
    String username;
    String password;
    PlayerCharacter character;
    public ArrayList<PlayerCharacter> AllPlayerCharacters = new ArrayList<PlayerCharacter>();
    int PlayerLevel;
    int HighestLevelReached;
    int TotalPlaythroughs;

    public Player(String user, String pwrd){
        this.username = user;
        this.password = pwrd;
        this.PlayerLevel = 1;
        this.HighestLevelReached = 0;
        this.TotalPlaythroughs = 0;
    }

    public void createNewCharacter(String name){
        this.character = new PlayerCharacter(this, name);
        AllPlayerCharacters.add(this.character);
    }

    public void createCustomCharacter(String name, int level, int health, int strength, int speed, int attack){
        this.character = new PlayerCharacter(this, name, level, health, strength, speed, attack);
        AllPlayerCharacters.add(this.character);
    }

    public void loadPrevCharacter(String name, int level){
        for(PlayerCharacter character : AllPlayerCharacters){
            if(Objects.equals(character.name, name) & character.level == level){
                this.character = character;
            }else{
                System.out.println("Character information not found");
            }
        }
    }
}
