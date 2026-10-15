package org.example.Character;

import org.example.Player.Player;
import org.example.card.Deck;

public class PlayerCharacter {
//    TODO: Finish PlayerCharacter implementation
    Player owner;

    public Deck deck;
    public String name;
    public int level;

    int Health;
    int Strength;
    int Speed;
    int Attack;
    int currency;

    public PlayerCharacter(Player owner, String name){
        this.owner = owner;
        this.name = name;
        this.deck = new Deck(this);
        this.level = 0;
        this.Health = 10;
        this.Strength = 5;
        this.Speed = 3;
        this.Attack = 5;
        this.currency = 0;
    }

    public PlayerCharacter(Player owner, String name, int level, int health, int strength, int speed, int attack, int currency){
        this.owner = owner;
        this.name = name;
        this.deck = new Deck(this);
        this.level = level;
        this.Health = health;
        this.Strength = strength;
        this.Speed = speed;
        this.Attack = attack;
        this.currency = currency;
    }


    public void permaChangeStat(String statName, int value){
        int stat = 4;
        switch(stat){
            case 1:
                statName = "HEALTH";
                this.Health = value;
                break;
            case 2:
                statName = "STRENGTH";
                this.Strength = value;
                break;
            case 3:
                statName = "SPEED";
                this.Speed = value;
                break;
            case 4:
                statName = "ATTACK";
                this.Attack = value;
                break;
        }
    }

    public void statBonus(String statName, int value){
        int stat = 4;
        switch(stat){
            case 1:
                statName = "HEALTH";
                this.Health = value;
                break;
            case 2:
                statName = "STRENGTH";
                this.Strength = value;
                break;
            case 3:
                statName = "SPEED";
                this.Speed = value;
                break;
            case 4:
                statName = "ATTACK";
                this.Attack = value;
                break;
        }
    }

    public int getStat(String statName){
        int stat = 4;
        switch(stat){
            case 1:
                statName = "HEALTH";
                return this.Health;
            case 2:
                statName = "STRENGTH";
                return this.Strength;
            case 3:
                statName = "SPEED";
                return this.Speed;
            case 4:
                statName = "ATTACK";
                return this.Attack;
        }
        return 0;
    }
}
