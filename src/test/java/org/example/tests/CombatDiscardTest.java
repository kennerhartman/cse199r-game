package org.example.tests;

import org.example.Character.PlayerCharacter;
import org.example.GameLoop.Combat;
import org.example.GameLoop.CombatAction;
import org.example.GameLoop.CombatState;
import org.example.Player.Player;
import org.example.card.ActionCard;
import org.example.card.Card;
import org.example.enemies.Enemy;
import org.example.map.MapNode;

public class CombatDiscardTest {

    public static void main(String[] args) {

        PlayerCharacter player =
                new PlayerCharacter(new Player(), "Test Knight");

        Enemy enemy =
                new Enemy(player, MapNode.NodeType.COMBAT);

        Combat combat = new Combat();
        combat.addCharacter(player);
        combat.addEnemy(enemy);

        // Strength +3 draws three additional cards.
        ActionCard strengthCard = new ActionCard(
                "Extra Draw",
                "Draw three additional cards",
                Card.CardStat.STRENGTH,
                3,
                0
        );

        player.deck.drawPile.add(strengthCard);

        // Supply enough cards to prevent reshuffling.
        for (int i = 0; i < 12; i++) {
            player.deck.drawPile.add(
                    new ActionCard(
                            "Card " + i,
                            "Test card",
                            Card.CardStat.HEALTH,
                            1,
                            0
                    )
            );
        }

        combat.startCombat();

        check("Initial hand has 5 cards",
                player.deck.hand.size() == 5);

        boolean queued = combat.queueAction(
                new CombatAction(player, strengthCard, enemy)
        );

        check("Strength card queued", queued);

        check("Strength draws 3 additional cards",
                player.deck.hand.size() == 8);

        combat.endTurn();

        // Strength card was played and removed from hand,
        // leaving 7 cards, above the normal limit of 5.
        check("Combat enters DISCARDING",
                combat.getState() == CombatState.DISCARDING);

        check("Turn has not advanced",
                combat.getTurnCount() == 1);

        Card firstDiscard = player.deck.hand.getLast();
        check("First discard succeeds",
                combat.discardCard(player, firstDiscard));

        check("Still discarding",
                combat.getState() == CombatState.DISCARDING);

        Card secondDiscard = player.deck.hand.getLast();
        check("Second discard succeeds",
                combat.discardCard(player, secondDiscard));

        check("Hand is back to limit",
                player.deck.hand.size() == 5);

        check("Next turn begins",
                combat.getState() == CombatState.PLAYER_TURN);

        check("Turn count advances to 2",
                combat.getTurnCount() == 2);

        System.out.println("Discard tests completed!");
    }

    private static void check(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError("[FAIL] " + message);
        }

        System.out.println("[PASS] " + message);
    }
}
