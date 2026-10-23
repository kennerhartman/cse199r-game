package org.example.map;

import org.example.Character.PlayerCharacter;
import org.example.card.CardStorage;

import java.util.ArrayList;
import java.util.List;


public class MapNode {
//    This class is just the node links of chosen nodes.
//    Next node is assigned when clicking a new node so that the chain is preserved
    public String ID;
    NodeType type;
    private String tooltip;

    private MapNode lastNode;
    private MapNode nextNode;

    private int nodesSinceBoss;
    private boolean isStartNode = false;


//    Creates a regular node
    public MapNode(String ID, NodeType type, String tooltip, MapNode last, int nodesSinceBoss){
        this.ID = ID;
        this.type = type;
        this.tooltip = tooltip;
        this.lastNode = last;
        this.nodesSinceBoss = nodesSinceBoss;
    }

//    Creates a starting node with a null lastNode
//    (This means that when searching nodes, you have to check isStart to stop searching last Node when you
//    hit the start again)
    public MapNode(String ID, NodeType type, String tooltip, int nodesSinceBoss, boolean isStart){
        this.ID = ID;
        this.type = type;
        this.tooltip = tooltip;
        this.nodesSinceBoss = nodesSinceBoss;
        this.isStartNode = isStart;
    }

    public void setNextNode(MapNode next){
        this.nextNode = next;
    }

    public int getNodesSinceBoss() {
        return nodesSinceBoss;
    }

    public String getID() {
        return ID;
    }

    public String getTooltip() {
        return tooltip;
    }

    public MapNode getLastNode() {
        return lastNode;
    }

    public MapNode getNextNode() {
        return nextNode;
    }

    public NodeType getType() {
        return type;
    }

    public enum NodeType {
        COMBAT,
        SHOP,
        BOSS
    }
}
