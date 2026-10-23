package org.example.map;

import org.example.Character.PlayerCharacter;
import org.example.card.CardStorage;
import org.example.enemies.Enemy;

public class Encounter {
//  This class needs work still, its unfinished obviously.

    public Encounter(){

    }

    public void createShop(CardStorage storage){
        storage.loadStore(5);
    }

    public Enemy createEnemy(PlayerCharacter target, MapNode.NodeType type){
        Enemy newEnemy = new Enemy(target, type);
        return newEnemy;
    }

    public void loadEncounter(){
//        TODO: Do encounter stuff here
    }
}
