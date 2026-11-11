package org.example.map;

import org.example.Character.PlayerCharacter;
import org.example.GameLoop.Combat;
import org.example.card.CardStorage;
import org.example.enemies.Enemy;

public class Encounter {
//  This class needs work still, its unfinished, obviously.
    MapNode.NodeType encounterType;
    public Encounter(MapNode newNode, PlayerCharacter character){
        this.encounterType = newNode.getType();
        switch(encounterType){
            case MapNode.NodeType.COMBAT:
                loadCombat(character);
            case MapNode.NodeType.BOSS:
                loadCombat(character);
//                TODO: Figure out how to pass in the storage best
//            case MapNode.NodeType.SHOP:
//                createShop(storage);
        }
    }

    public void createShop(CardStorage storage){
        storage.loadStore(5);
    }

    private Enemy createEnemy(PlayerCharacter target){
        Enemy newEnemy = new Enemy(target, encounterType);
        return newEnemy;
    }

    public void loadCombat(PlayerCharacter target){
        Combat newCombat = new Combat();
//        TODO: Add logic for multiple characters and multiple enemies
        newCombat.addCharacter(target);
        newCombat.addEnemy(createEnemy(target));
        newCombat.startCombat();
    }
}
