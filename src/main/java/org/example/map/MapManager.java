package org.example.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager {
//    This class handles all of the map generation and choice storage.
    private MapNode currentNode;
    private List<MapNode> activeChoices;
    private List<String> shopTips = List.of("Shop!", "A shady merchant awaits...", "Rest and Restock!", "Spend your coins here!", "Curios for sale!");
    private List<String> combatTips = List.of("Looks like a fight...", "Trouble ahead!", "Hostile presence detected.", "Unfamiliar footsteps echo...", "Monsters block the path!");
    private List<String> bossTips = List.of("An ominous aura looms...", "Danger: Boss Ahead!", "Prepare yourself...", "Test your mettle!", "This guy looks big...");

    public MapManager(){
//        Initialize
        this.activeChoices = new ArrayList<>();
//        Set current node to new start node with no last node
        currentNode = new MapNode ("Tavern", MapNode.NodeType.COMBAT, "Starting Area!", 0, true);
//        Set choices to either start a shop if they don't know what they're doing, or go to a real combat which starts the normal loop
        activeChoices.add(new MapNode("Merchant", MapNode.NodeType.SHOP, "A Merchant on the road...", currentNode, 0));
        activeChoices.add(new MapNode("Combat1", MapNode.NodeType.COMBAT, "Start Your Journey! (Skip the tutorial...)", currentNode, 0));
    }

    public void selectNode(MapNode chosenNode){
//        Set the currentNodes next node to be the chosen node, then switch it to the new node
        this.currentNode.setNextNode(chosenNode);
        this.currentNode = chosenNode;
//        Clear the old choices
        this.activeChoices.clear();
//        set the new choices- if you just finished the new shop, start a new cycle, if not generate normally
        if(currentNode.getID().equals("Merchant")){
            activeChoices.add(new MapNode("combat1", MapNode.NodeType.COMBAT, "Start Your Journey!", currentNode, 0));
        }else{
            addEdges();
        }
//        TODO: Start an encounter, which should be the fight?
    }

    public String generateID(String currentID){
        int number = (int) currentID.charAt(currentID.length() -1) +1;
        return String.valueOf(number);
    }

    public String getToolTip(MapNode.NodeType type){
        Random rand = new Random(5);
        int num = rand.nextInt();
        switch(type){
            case MapNode.NodeType.COMBAT:
                return combatTips.get(num);
            case MapNode.NodeType.SHOP:
                return shopTips.get(num);
            case MapNode.NodeType.BOSS:
                return bossTips.get(num);
        }
        return "<Type Mismatch in MapManager.getToolTip()>";
    }

//    Returns available next nodes
    public List<MapNode> getActiveChoices() {
        return activeChoices;
    }

//    Generates new edge nodes and adds them to activeChoices based on the current node
    public void addEdges() {
        boolean evenNode = (currentNode.getNodesSinceBoss() % 2) == 0;
        boolean bossTime = (currentNode.getNodesSinceBoss() >= 3);
        String id = generateID(currentNode.getID());
        if (currentNode.getType() == MapNode.NodeType.BOSS) {
            // Start a new cycle after the boss, initial combat should be 0
            MapNode combat = new MapNode(
                    "combat"+id,
                    MapNode.NodeType.COMBAT,
                    getToolTip(MapNode.NodeType.COMBAT),
                    this.currentNode,
                    0
            );
            activeChoices.add(combat);

        } else if (currentNode.getType() == MapNode.NodeType.COMBAT && bossTime) {
            // The next node is the boss after at least 3 completed nodes
            MapNode boss = new MapNode(
                    "boss"+id,
                    MapNode.NodeType.BOSS,
                    getToolTip(MapNode.NodeType.BOSS),
                    this.currentNode,
                    -1
            );
            activeChoices.add(boss);

        } else if (currentNode.getType() == MapNode.NodeType.COMBAT && evenNode) {
            // Even-numbered combat nodes can lead to either a shop or combat
            MapNode shop = new MapNode(
                    "shop"+id,
                    MapNode.NodeType.SHOP,
                    getToolTip(MapNode.NodeType.SHOP),
                    this.currentNode,
                    currentNode.getNodesSinceBoss() + 1
            );
            MapNode combat = new MapNode(
                    "combat"+id,
                    MapNode.NodeType.COMBAT,
                    getToolTip(MapNode.NodeType.COMBAT),
                    this.currentNode,
                    currentNode.getNodesSinceBoss() + 1
            );

            activeChoices.add(shop);
            activeChoices.add(combat);

        } else if (currentNode.getType() == MapNode.NodeType.SHOP) {
            if (bossTime) {
                MapNode boss = new MapNode(
                        "boss"+id,
                        MapNode.NodeType.BOSS,
                        getToolTip(MapNode.NodeType.BOSS),
                        this.currentNode,
                        -1
                );
                activeChoices.add(boss);
            } else {
                MapNode combat = new MapNode(
                        "combat"+id,
                        MapNode.NodeType.COMBAT,
                        getToolTip(MapNode.NodeType.COMBAT),
                        this.currentNode,
                        currentNode.getNodesSinceBoss() + 1
                );
                activeChoices.add(combat);
            }
        }else if (currentNode.getType() == MapNode.NodeType.COMBAT) {
            // Odd-numbered combat nodes lead to another combat
            MapNode combat = new MapNode(
                    "combat"+id,
                    MapNode.NodeType.COMBAT,
                    getToolTip(MapNode.NodeType.COMBAT),
                    this.currentNode,
                    currentNode.getNodesSinceBoss() + 1
            );
            activeChoices.add(combat);

        } else {
            System.out.println("There was an error in MapNode.addEdges()");
        }
    }
}
