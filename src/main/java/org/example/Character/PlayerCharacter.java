package org.example.Character;

import java.util.ArrayList;

import org.example.Player.Player;
import org.example.card.Card;
import org.example.card.Deck;

public class PlayerCharacter {
//    TODO: Finish PlayerCharacter implementation
    Player owner;

    public Deck deck;
    public String name;
    public int level;
    public String spriteId;

    int Health;
    int Strength;
    int Speed;
    int Attack;

    int currency;

//    Updated to work with old constructors and give default knight sprite to those without a specified ID

    public PlayerCharacter(Player owner, String name) {
        this(owner, name, "knight");
    }

    public PlayerCharacter(Player owner, String name, String spriteId) {
        this.owner = owner;
        this.name = name;
        this.spriteId = spriteId;

        this.deck = new Deck(this);
        this.level = 0;
        this.Health = 10;
        this.Strength = 5;
        this.Speed = 3;
        this.Attack = 5;
        this.currency = 0;
    }

    public PlayerCharacter(
            Player owner,
            String name,
            int level,
            int health,
            int strength,
            int speed,
            int attack,
            int currency
    ) {
        this(owner, name, level, health, strength,
                speed, attack, currency, "knight");
    }

    public PlayerCharacter(
            Player owner,
            String name,
            int level,
            int health,
            int strength,
            int speed,
            int attack,
            int currency,
            String spriteId
    ) {
        this.owner = owner;
        this.name = name;
        this.spriteId = spriteId;

        this.deck = new Deck(this);
        this.level = level;
        this.Health = health;
        this.Strength = strength;
        this.Speed = speed;
        this.Attack = attack;
        this.currency = currency;
    }


    public void permaChangeStat(String statName, int value){
        switch(statName){
            case "HEALTH":
                this.Health = value;
                break;
            case "STRENGTH":
                this.Strength = value;
                break;
            case "SPEED":
                this.Speed = value;
                break;
            case "ATTACK":
                this.Attack = value;
                break;
        }
    }

    public void statBonus(String statName, int value){
        switch(statName){
            case "HEALTH":
                this.Health = value;
                break;
            case "STRENGTH":
                this.Strength = value;
                break;
            case "SPEED":
                this.Speed = value;
                break;
            case "ATTACK":
                this.Attack = value;
                break;
        }
    }

    public int getStat(String statName){
        switch(statName){
            case "HEALTH":
                return this.Health;
            case "STRENGTH":
                return this.Strength;
            case "SPEED":
                return this.Speed;
            case "ATTACK":
                return this.Attack;
        }
        return 0;
    }

    public int getLevel(){
        return this.level;
    }
  
    public ArrayList<Card> getDrawPile() {
        return this.deck.drawPile;
    }
}
