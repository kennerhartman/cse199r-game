
package org.example.tests;

import org.example.Character.PlayerCharacter;
import org.example.GameLoop.Combat;
import org.example.GameLoop.CombatAction;
import org.example.GameLoop.CombatState;
import org.example.Player.Player;
import org.example.card.ActionCard;
import org.example.card.Card;
import org.example.map.Encounter;
import org.example.map.MapManager;
import org.example.map.MapNode;

public class EncounterIntegrationTest {

    public static void main(String[] args) {

        System.out.println("=== ENCOUNTER INTEGRATION TEST ===");

        // Create a player and character.
        PlayerCharacter player =
                new PlayerCharacter(new Player(), "Test Knight");

        // Create the map.
        MapManager mapManager = new MapManager();

        // Give the player a powerful attack card.
        ActionCard attackCard = new ActionCard(
                "Finishing Blow",
                "Deals enough damage to defeat an enemy",
                Card.CardStat.ATTACK,
                100,
                0
        );

        player.deck.drawPile.add(attackCard);

        // Find the first available combat node.
        MapNode combatNode = null;

        for (MapNode node : mapManager.getActiveChoices()) {
            if (node.getType() == MapNode.NodeType.COMBAT) {
                combatNode = node;
                break;
            }
        }

        check("Combat node exists", combatNode != null);

        // Select the combat node.
        mapManager.selectNode(combatNode, player);

        // Retrieve the encounter created by MapManager.
        Encounter encounter = mapManager.getCurrentEncounter();

        check("Encounter created", encounter != null);

        check(
                "Encounter has correct type",
                encounter.getType() == MapNode.NodeType.COMBAT
        );

        // Start the encounter.
        encounter.startEncounter();

        Combat combat = encounter.getCombat();

        check(
                "Encounter is not completed when combat starts",
                !encounter.isCompleted()
        );

        // Find the next available node.
        MapNode nextNode = mapManager.getActiveChoices().getFirst();

// Remember the current location.
        MapNode originalNode = mapManager.getCurrentNode();

        // Attempt to move while combat is still active.
        mapManager.selectNode(nextNode, player);

        check(
                "Current encounter remains unchanged",
                mapManager.getCurrentEncounter() == encounter
        );

        check("Combat created", combat != null);

        check(
                "Combat started",
                combat.getState() == CombatState.PLAYER_TURN
        );

        check(
                "Player added to combat",
                combat.getCharacters().contains(player)
        );

        check(
                "Enemy added to combat",
                !combat.getEnemies().isEmpty()
        );

        // Queue the powerful attack.
        boolean queued = combat.queueAction(
                new CombatAction(
                        player,
                        attackCard,
                        combat.getEnemies().getFirst()
                )
        );

        check("Attack card queued", queued);

        // Resolve the turn.
        combat.endTurn();

        check(
                "Combat ends in victory",
                combat.getState() == CombatState.VICTORY
        );

        check(
                "Enemy defeated",
                combat.getEnemies().getFirst().getHealth() <= 0
        );

        check(
                "Encounter completed after victory",
                encounter.isCompleted()
        );

        // Move to the next node after winning combat.
        mapManager.selectNode(nextNode, player);

        check(
                "Map advances after victory",
                mapManager.getCurrentNode() == nextNode
        );

        check(
                "A new encounter is created",
                mapManager.getCurrentEncounter() != encounter
        );

        check(
                "New encounter has not started yet",
                !mapManager.getCurrentEncounter().hasStarted()
        );

        System.out.println(
                "\nAll encounter integration tests passed!"
        );
    }

    private static void check(String description, boolean condition) {

        if (!condition) {
            throw new AssertionError(
                    "[FAIL] " + description
            );
        }

        System.out.println("[PASS] " + description);
    }
}
