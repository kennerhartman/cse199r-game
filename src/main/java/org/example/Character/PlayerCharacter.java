package org.example.Character;

import java.util.ArrayList;

import org.example.Player.Player;
import org.example.card.Card;
import org.example.card.Deck;
import org.example.enemies.Enemy;

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

    private int currentHealth;

    public PlayerCharacter(Player owner, String name){
        this.owner = owner;
        this.name = name;
        this.deck = new Deck(this);
        this.level = 0;
        this.Health = 10;
        this.currentHealth = this.Health;
        this.Strength = 5;
        this.Speed = 3;
        this.Attack = 5;
        this.currency = 0;
    }

    public PlayerCharacter(Player owner, String name, int level, int health, int strength, int speed, int attack, int currency, int currentHealth){
        this.owner = owner;
        this.name = name;
        this.deck = new Deck(this);
        this.level = level;
        this.Health = health;
        this.currentHealth = currentHealth;
        this.Strength = strength;
        this.Speed = speed;
        this.Attack = attack;
        this.currency = currency;
    }

    public void attack(Enemy target){
        target.giveDamage(this.Attack);
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

    public void statBonus(String statName, int value) {
        switch (statName) {
            case "HEALTH" -> changeMaxHealth(value);
            case "STRENGTH" -> this.Strength += value;
            case "SPEED" -> this.Speed += value;
            case "ATTACK" -> this.Attack += value;

            default -> throw new IllegalArgumentException(
                    "Unknown stat: " + statName
            );
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


    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return Health;
    }

    // Deals damage without allowing negative health
    public void takeDamage(int damage) {
        if (damage <= 0) {
            return;
        }

        currentHealth = Math.max(0, currentHealth - damage);
    }

    // Restores health without exceeding maximum health
    public void heal(int amount) {
        if (amount <= 0) {
            return;
        }

        currentHealth = Math.min(Health, currentHealth + amount);
    }

    private void changeMaxHealth(int amount) {
        this.Health = Math.max(1, this.Health + amount);

        if (amount > 0) {
            this.currentHealth = Math.min(this.Health, this.currentHealth + amount);
        } else {
            this.currentHealth = Math.min(this.currentHealth, this.Health);
        }
    }

    // Restores the character to full health
    public void restoreHealth() {
        currentHealth = Health;
    }

    // Checks whether the character is alive
    public boolean isAlive() {
        return currentHealth > 0;
    }

}
