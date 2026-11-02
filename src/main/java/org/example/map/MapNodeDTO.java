package org.example.map;

public class MapNodeDTO {
    public String ID;
    public String type;
    public String tooltip;

    public String lastNode;
    public String nextNode;

    public int nodesSinceBoss;
    public boolean isStartNode = false;

    public int currentTile;

    public MapNodeDTO(MapNode mapNode){
        this.ID = mapNode.getID();
        this.type = mapNode.getType().toString();
        this.tooltip = mapNode.getTooltip();
        this.lastNode = mapNode.getLastNode() != null
                ? mapNode.getLastNode().getID()
                : null;
        this.nextNode = mapNode.getNextNode() != null
                ? mapNode.getNextNode().getID()
                : null;
        this.nodesSinceBoss = mapNode.getNodesSinceBoss();
        this.isStartNode = mapNode.checkIsStart();
        this.currentTile = mapNode.getTile();
    }
}
