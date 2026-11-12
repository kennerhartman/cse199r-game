package org.example.Character;


import org.example.Player.Player;
import org.example.card.Card;
import org.example.map.MapManager;
import org.example.map.MapNodeDTO;

import java.util.ArrayList;
import java.util.List;

public class GameSaveData {
    public String owner;

    public ArrayList<String> deck = new ArrayList<String>();
    public String name;
    public int level;

    public int Health;
    public int Strength;
    public int Speed;
    public int Attack;
    public int currency;
    public String spriteId;
//    TODO: Add in game save data, such as map node, etc.

    public String currentNodeID;
    public int currentNodesSinceBoss;
    public List<MapNodeDTO> allNodes = new ArrayList<>();

    public GameSaveData(PlayerCharacter character, Player player, MapManager mapManager){
        this.owner = player.username;
        for(Card card : character.deck.drawPile){
            this.deck.add(card.name);
            System.out.println("Card: "+card.name+" added");
        }
        this.name = character.name;
        this.level = character.level;
        this.Health = character.Health;
        this.Strength = character.Strength;
        this.Speed = character.Speed;
        this.Attack = character.Attack;
        this.currency = character.currency;
        this.spriteId = character.spriteId;

        this.currentNodeID = mapManager.getCurrentNode().getID();
        this.currentNodesSinceBoss = mapManager.getCurrentNode().getNodesSinceBoss();
        this.allNodes = mapManager.getSaveData();
    }
}
