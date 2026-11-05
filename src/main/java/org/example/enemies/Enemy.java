package org.example.enemies;

import org.example.Character.PlayerCharacter;
import org.example.map.MapNode;

import java.util.Random;


public class Enemy {
    private int health;
    private int attacks;
    private int damage;
    private int coins;
    private boolean dropsLoot;

    private final int BASE_HEALTH = 10;
    private final int BASE_DAMAGE = 2;
    private final int BASE_ATTACKS = 1;
    private final int DROP_CHANCE_PERCENT = 20;

    public Enemy(PlayerCharacter target, MapNode.NodeType type){
        int level = target.getLevel();

        if(type == MapNode.NodeType.COMBAT){
            generateBasicEnemy(level);
        }else if(type == MapNode.NodeType.BOSS){
            generateBoss(level);
        }else{
            throw new IllegalArgumentException("Unsupported MapNode.NodeType: " + type);
        }
    }

    private void generateBasicEnemy(int level) {
        Random rand = new Random();

        // Expo. Scaling
        // Level 1: HP = 10. Level 10: HP ≈ 44. Level 50: HP ≈ 640.
        this.health = (int) (BASE_HEALTH * Math.pow(1.08, level - 1) + (level * 2));
        this.damage = (int) (BASE_DAMAGE * Math.pow(1.05, level - 1) + (level / 2));

        // Caps attacks
        this.attacks = BASE_ATTACKS + (level / 20);

        // Gold scales based on enemy hp/player effort to KO
        this.coins = Math.max(1, this.health / 3);

        // Loot drop
        this.dropsLoot = rand.nextInt(100) < DROP_CHANCE_PERCENT;
    }

    private void generateBoss(int level) {
        Random rand = new Random();

        // Bosses are more dangerous
        // Level 1: HP = 50. Level 10: HP ≈ 260. Level 50: HP ≈ 13,000.
        this.health = (int) (BASE_HEALTH * 5 * Math.pow(1.15, level - 1) + (level * 10));
        this.damage = (int) (BASE_DAMAGE * 2 * Math.pow(1.08, level - 1) + level);

        // Bosses get more attacks
        this.attacks = BASE_ATTACKS + 1 + (level / 15);

        // More coins from Bosses
        this.coins = Math.max(5, this.health / 2);

        // Bosses have higher loot drop rate
        this.dropsLoot = rand.nextInt(100) < (DROP_CHANCE_PERCENT * 2);
    }

    // --- Getters ---
    public int getHealth() { return this.health; }
    public int getAttacks() { return this.attacks; }
    public int getDamage() { return this.damage; }
    public int getCoins() { return this.coins; }
    public boolean checkLoot() { return this.dropsLoot; }

    // --- Setters ---
    public void giveDamage(int damage) { this.health=this.health-damage; }
}
