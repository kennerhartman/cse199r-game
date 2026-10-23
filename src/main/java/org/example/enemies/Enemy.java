package org.example.enemies;

import org.example.Character.PlayerCharacter;
import org.example.map.MapNode;

import java.util.Random;


public class Enemy {
     int health = 10;
     int attacks = 1;
     int damage = 2;
     int coins = health/2;
     int dropChance = 20;
     boolean dropsLoot;

    public Enemy(PlayerCharacter target, MapNode.NodeType type){
        int level = target.getLevel();
        if(type == MapNode.NodeType.BOSS){
            basicEnemy(level);
        }else if(type == MapNode.NodeType.COMBAT){
            boss(level);
        }else{
//            TODO: throw error
        }
    }

    public void basicEnemy(int level){
        //        TODO: Tweak auto generation to make sense with character progression
        Random rand = new Random();
        int modifier = (int) Math.floor(level*.6);
        this.health *= modifier;
        this.attacks *= modifier;
        this.damage *= modifier;
        this.coins = health/2;
        int randomChance = rand.nextInt(100);
        if(randomChance>=dropChance){
            this.dropsLoot = true;
        }else{
            this.dropsLoot = false;
        }
    }

    public void boss(int level){
//        TODO: Tweak auto generation to make sense with character progression
        Random rand = new Random();
        int modifier = (int) Math.floor(level*1.6);
        this.health *= modifier;
        this.attacks *= modifier;
        this.damage *= modifier;
        this.coins *= modifier;
        int randomChance = rand.nextInt(100);
        if(randomChance>=dropChance){
            this.dropsLoot = true;
        }else{
            this.dropsLoot = false;
        }
    }

    public int getHealth(){
        return this.health;
    }

    public int getAttacks(){
        return this.attacks;
    }

    public int getDamage(){
        return this.damage;
    }

    public int getCoins(){
        return this.coins;
    }

    public boolean checkLoot(){
        return this.dropsLoot;
    }
}
