
package org.example.map;

import org.example.Character.PlayerCharacter;
import org.example.GameLoop.Combat;
import org.example.GameLoop.CombatState;
import org.example.client.Client;
import org.example.enemies.Enemy;

public class Encounter {

    // Information about this encounter
    private final MapNode parentNode;
    private final MapNode.NodeType encounterType;
    private final PlayerCharacter character;

    // Combat is null if this is not a combat encounter
    private Combat combat;

    // Has this encounter been started?
    private boolean started = false;

    public Encounter(MapNode newNode, PlayerCharacter character) {
        this.parentNode = newNode;
        this.encounterType = newNode.getType();
        this.character = character;
    }

    // Called when the game is ready to begin the encounter
    public void startEncounter() {

        // Prevent accidentally starting twice
        if (started) {
            return;
        }

        started = true;

        switch (encounterType) {

            case COMBAT, BOSS -> loadCombat();

            case SHOP -> createShop();

            default -> {
                System.out.println("Unknown encounter type!");
            }
        }
    }

    private void createShop() {
        Client.getInstance().cardStorage.loadStore(5);
    }

    private Enemy createEnemy() {
        return new Enemy(character, encounterType);
    }

    private void loadCombat() {
        this.combat = new Combat();

        combat.addCharacter(character);
        combat.addEnemy(createEnemy());

        combat.startCombat();
    }

    // --- Getters ---

    public MapNode getParentNode() {
        return parentNode;
    }

    public MapNode.NodeType getType() {
        return encounterType;
    }

    public Combat getCombat() {
        return combat;
    }

    public boolean hasStarted() {
        return started;
    }

    public boolean isCompleted() {

        if (!started) {
            return false;
        }

        switch (encounterType) {

            case COMBAT, BOSS -> {
                return combat != null
                        && combat.getState() == CombatState.VICTORY;
            }

            case SHOP -> {
                // TODO: Connect this to the shop exit logic.
                return false;
            }

            default -> {
                return false;
            }
        }
    }
}
