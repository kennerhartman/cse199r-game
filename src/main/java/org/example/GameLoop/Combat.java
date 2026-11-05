package org.example.GameLoop;

import org.example.Character.PlayerCharacter;
import org.example.enemies.Enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Combat {
    private List<PlayerCharacter> characters = new ArrayList<>();
    private List<Integer> playerhealths = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private int turnCount = 0;

    public Combat(){}

    public void addCharacter(PlayerCharacter player){
        this.characters.add(player);
        this.playerhealths.add(player.getStat("HEALTH"));
    }

    public void addEnemy(Enemy enemy){
        this.enemies.add(enemy);
    }

    public void startCombat(){
        Random rand = new Random();
        while(enemies.getFirst().getHealth()>0){
            for(PlayerCharacter player:characters){
                player.deck.fillHand();
            }
            for(Enemy enemy:enemies){
                for(int i = 0; i<enemy.getAttacks(); i++){
//                    Get a random number within the bounds of the character and health storage lists
                    int choice = rand.nextInt(0, characters.size()-1);
//                    Select the random player and health that corresponds with them
                    PlayerCharacter randomPlayer = characters.get(choice);
//                    attack them and change the health
                    int randomHealth = playerhealths.get(choice) - enemy.getDamage();

                    if(randomHealth<=0){
//                        Remove the character and health value in the list if they die
                        characters.remove(choice);
                        playerhealths.remove(choice);
                    }else{
//                        Adjust the health in the array list
                        playerhealths.set(choice, randomHealth);
                    }
                }
            }
            for(PlayerCharacter player:characters){
                for(int i = 0; i<player.getStat("SPEED"); i++){
//                    TODO: Fill in Selection Logic
//                    Get selected card
//                    player.deck.play(player);
                }
//                Get selected enemy
//                player.attack(selectedEnemy);
            }
        }
    }
}
