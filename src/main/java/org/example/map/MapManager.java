package org.example.map;

import org.example.Character.PlayerCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        currentNode = new MapNode ("Tavern", MapNode.NodeType.COMBAT, "Starting Area!", 0, true, 0);
//        Set choices to either start a shop if they don't know what they're doing, or go to a real combat which starts the normal loop
        activeChoices.add(new MapNode("Merchant", MapNode.NodeType.SHOP, "A Merchant on the road...", currentNode, 0, 0));
        activeChoices.add(new MapNode("combat0", MapNode.NodeType.COMBAT, "Start Your Journey! (Skip the tutorial...)", currentNode, 0, 0));
    }

    public MapManager(List<MapNodeDTO> mapSave){
        this.activeChoices = new ArrayList<>();
        int i = 0;
        MapNode lastCreated = null;
        for(MapNodeDTO dto:mapSave){
            MapNode newNode = new MapNode(dto.ID, MapNode.NodeType.valueOf(dto.type), dto.tooltip, dto.nodesSinceBoss, dto.isStartNode, dto.currentTile);
            if(i == 0 && !dto.isStartNode){
                lastCreated = newNode;
                this.currentNode = newNode;
                i++;
            }else{
                newNode.setNextNode(lastCreated);
                lastCreated = newNode;
            }
        }
        addEdges();
    }

    private void setCurrentNode(MapNode newCurrent){ this.currentNode = newCurrent; }
    private void setActiveChoices(List<MapNode> newActive){ this.activeChoices = newActive; }

//    Called when a node is clicked on, pass in the chose node
    public void selectNode(MapNode chosenNode, PlayerCharacter character){
//        Set the currentNodes next node to be the chosen node, then switch it to the new node
        this.currentNode.setNextNode(chosenNode);
        this.currentNode = chosenNode;
//        Clear the old choices
        this.activeChoices.clear();
//        set the new choices- if you just finished the new shop, start a new cycle, if not generate normally
        if(currentNode.getID().equals("Merchant")){
            activeChoices.add(new MapNode("combat0", MapNode.NodeType.COMBAT, "Start Your Journey!", currentNode, 0, 0));
        }else{
            addEdges();
        }
        System.out.println("Edges Added\nLoad Encounter...");
//        TODO: Start an encounter, which should be the fight?
        Encounter newEncounter = new Encounter(currentNode, character);
    }

    public String generateID(String currentID){
        Pattern ID_PATTERN = Pattern.compile("^(.*?)(\\d+)$");
        if (currentID == null || currentID.isEmpty()) {
            throw new IllegalArgumentException("ID string cannot be null or empty");
        }
        Matcher matcher = ID_PATTERN.matcher(currentID);
        if (matcher.find()) {
            String prefix = matcher.group(1);      // e.g., "player_score_" or "item"
            String numberStr = matcher.group(2);    // e.g., "095" or "123"

            // 1. Convert to integer and increment
            int nextNumber = Integer.parseInt(numberStr) + 1;

            // 2. Format with the same amount of padding if your IDs use leading zeros (e.g., "095" -> "096")
            String nextNumberStr = String.format("%0" + numberStr.length() + "d", nextNumber);

            // 3. Return the fully formed next sequential ID
            return nextNumberStr;
        }

        throw new IllegalArgumentException("ID does not end with a valid sequential integer: " + currentID);
    }

    public String getToolTip(MapNode.NodeType type){
        Random rand = new Random();
        int num = rand.nextInt(4);
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

    public MapNode getCurrentNode(){ return currentNode;}

    public List<MapNode> getPath(){
//        Create a list to store the path, store current node
        List<MapNode> path = new ArrayList<>();
        MapNode newNode = currentNode;
        boolean pathCheck = true;
//        path.add(newNode);
//        Until you hit the start node, add each one and get the node before it
        while(pathCheck){
            path.add(newNode);
            if(!newNode.checkIsStart()){
                newNode = newNode.getLastNode();
            }else{
                pathCheck = false;
            }
        }
//        Return the list of nodes.
        return path;
    }

    public List<MapNodeDTO> getSaveData(){
        List<MapNodeDTO> saveData = new ArrayList<>();
        for(MapNode newNode: this.getPath()){
            saveData.add(new MapNodeDTO(newNode));
        }
        return saveData;
    }

//    Generates new edge nodes and adds them to activeChoices based on the current node
    public void addEdges() {
        boolean evenNode = (currentNode.getNodesSinceBoss() % 2) == 0;
        boolean bossTime = (currentNode.getNodesSinceBoss() >= 3);
        int currentTile = currentNode.getTile();
        String id = generateID(currentNode.getID());
        if (currentNode.getType() == MapNode.NodeType.BOSS) {
            // Start a new cycle after the boss, initial combat should be 0
            MapNode combat = new MapNode(
                    "combat"+id,
                    MapNode.NodeType.COMBAT,
                    getToolTip(MapNode.NodeType.COMBAT),
                    this.currentNode,
                    0,
                    currentTile+1
            );
            activeChoices.add(combat);

        } else if (currentNode.getType() == MapNode.NodeType.COMBAT && bossTime) {
            // The next node is the boss after at least 3 completed nodes
            MapNode boss = new MapNode(
                    "boss"+id,
                    MapNode.NodeType.BOSS,
                    getToolTip(MapNode.NodeType.BOSS),
                    this.currentNode,
                    -1,
                    currentTile
            );
            activeChoices.add(boss);

        } else if (currentNode.getType() == MapNode.NodeType.COMBAT && evenNode) {
            // Even-numbered combat nodes can lead to either a shop or combat
            MapNode shop = new MapNode(
                    "shop"+id,
                    MapNode.NodeType.SHOP,
                    getToolTip(MapNode.NodeType.SHOP),
                    this.currentNode,
                    currentNode.getNodesSinceBoss() + 1,
                    currentTile
            );
            MapNode combat = new MapNode(
                    "combat"+id,
                    MapNode.NodeType.COMBAT,
                    getToolTip(MapNode.NodeType.COMBAT),
                    this.currentNode,
                    currentNode.getNodesSinceBoss() + 1,
                    currentTile
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
                        -1,
                        currentTile
                );
                activeChoices.add(boss);
            } else {
                MapNode combat = new MapNode(
                        "combat"+id,
                        MapNode.NodeType.COMBAT,
                        getToolTip(MapNode.NodeType.COMBAT),
                        this.currentNode,
                        currentNode.getNodesSinceBoss() + 1,
                        currentTile
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
                    currentNode.getNodesSinceBoss() + 1,
                    currentTile
            );
            activeChoices.add(combat);

        } else {
            System.out.println("There was an error in MapNode.addEdges()");
        }
    }

}
