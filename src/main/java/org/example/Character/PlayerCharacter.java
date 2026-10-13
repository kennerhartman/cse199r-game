package org.example.Character;

import java.util.ArrayList;

import org.example.Player.Player;
import org.example.card.Card;
import org.example.card.Deck;

public class PlayerCharacter {
//    TODO: Finish PlayerCharacter implementation
    Player owner;

    Deck deck;
    public String name;

    public int level;
    int Health;
    int Strength;
    int Speed;
    int Attack;

    public PlayerCharacter(Player owner, String name){
        this.owner = owner;
        this.name = name;
        this.deck = new Deck(this);
        this.level = 0;
        this.Health = 10;
        this.Strength = 5;
        this.Speed = 3;
        this.Attack = 5;
    }

    public PlayerCharacter(Player owner, String name, int level, int health, int strength, int speed, int attack){
        this.owner = owner;
        this.name = name;
        this.deck = new Deck(this);
        this.level = level;
        this.Health = health;
        this.Strength = strength;
        this.Speed = speed;
        this.Attack = attack;
    }

    public ArrayList<Card> getHand() {
        return this.deck.hand;
    }
}
