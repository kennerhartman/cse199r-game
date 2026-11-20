package org.example.tests;

import org.example.Character.PlayerCharacter;
import org.example.GameLoop.Combat;
import org.example.GameLoop.CombatAction;
import org.example.Player.Player;
import org.example.card.ActionCard;
import org.example.card.Card;
import org.example.enemies.Enemy;
import org.example.map.MapNode;

public class CombatPlanningTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println("=== COMBAT PLANNING TESTS ===");

        testSpeedCard();
        testStrengthCard();
        testSpeedLimit();
        testDuplicateCard();
        testInvalidState();
        testUndoSpeed();
        testUndoStrength();

        System.out.println("\n=== RESULTS ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                    failed + " combat planning tests failed."
            );
        }
    }

    // Creates a new player for each test so that
    // previous tests don't affect later tests.
    private static PlayerCharacter createPlayer() {
        return new PlayerCharacter(new Player(), "Test Knight");
    }

    private static Combat createCombat(PlayerCharacter player) {
        Combat combat = new Combat();

        combat.addCharacter(player);
        combat.addEnemy(
                new Enemy(player, MapNode.NodeType.COMBAT)
        );

        return combat;
    }

    private static ActionCard createCard(
            String name,
            Card.CardStat stat,
            int bonus
    ) {
        return new ActionCard(
                name,
                "Test card",
                stat,
                bonus,
                0
        );
    }

    private static void check(
            String description,
            boolean condition
    ) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + description);
        } else {
            failed++;
            System.out.println("[FAIL] " + description);
        }
    }

    // TEST 1: Speed increases available actions
    private static void testSpeedCard() {

        PlayerCharacter player = createPlayer();
        Combat combat = createCombat(player);

        ActionCard speedCard =
                createCard("Quick Thinking", Card.CardStat.SPEED, 2);

        player.deck.drawPile.add(speedCard);

        combat.startCombat();

        int startingActions = combat.getAvailableActions(player);

        boolean queued = combat.queueAction(
                new CombatAction(player, speedCard, combat.getEnemies().getFirst())
        );

        check("Speed card queues successfully", queued);

        check(
                "Speed increases available actions from 3 to 5",
                startingActions == 3
                        && combat.getAvailableActions(player) == 5
        );
    }

    // TEST 2: Strength draws additional cards immediately
    private static void testStrengthCard() {

        PlayerCharacter player = createPlayer();
        Combat combat = createCombat(player);

        ActionCard strengthCard =
                createCard("Battle Focus", Card.CardStat.STRENGTH, 2);

        player.deck.drawPile.add(strengthCard);

        // Add enough extra cards for the Strength effect.
        for (int i = 0; i < 6; i++) {
            player.deck.drawPile.add(
                    createCard("Attack " + i, Card.CardStat.ATTACK, 1)
            );
        }

        combat.startCombat();

        int handBefore = player.deck.hand.size();

        boolean queued = combat.queueAction(
                new CombatAction(player, strengthCard, combat.getEnemies().getFirst())
        );

        check("Strength card queues successfully", queued);

        check(
                "Strength draws two additional cards",
                player.deck.hand.size() == handBefore + 2
        );
    }

    // TEST 3: SPEED limits how many cards can be queued
    private static void testSpeedLimit() {

        PlayerCharacter player = createPlayer();
        Combat combat = createCombat(player);

        for (int i = 0; i < 5; i++) {
            player.deck.drawPile.add(
                    createCard("Attack " + i, Card.CardStat.ATTACK, 1)
            );
        }

        combat.startCombat();

        Enemy enemy = combat.getEnemies().getFirst();

        for (int i = 0; i < 3; i++) {
            boolean result = combat.queueAction(
                    new CombatAction(player, player.deck.hand.get(i), enemy)
            );

            check("Action " + (i + 1) + " accepted", result);
        }

        boolean fourthAction = combat.queueAction(
                new CombatAction(player, player.deck.hand.get(3), enemy)
        );

        check("Fourth action rejected at SPEED 3", !fourthAction);
    }

    // TEST 4: The same card cannot be queued twice
    private static void testDuplicateCard() {

        PlayerCharacter player = createPlayer();
        Combat combat = createCombat(player);

        ActionCard attackCard =
                createCard("Single Attack", Card.CardStat.ATTACK, 2);

        player.deck.drawPile.add(attackCard);

        combat.startCombat();

        Enemy enemy = combat.getEnemies().getFirst();

        CombatAction action =
                new CombatAction(player, attackCard, enemy);

        boolean first = combat.queueAction(action);
        boolean second = combat.queueAction(action);

        check("First queue accepted", first);
        check("Duplicate queue rejected", !second);
    }

    // TEST 5: Cards cannot be queued before combat starts
    private static void testInvalidState() {

        PlayerCharacter player = createPlayer();
        Combat combat = createCombat(player);

        ActionCard attackCard =
                createCard("Attack", Card.CardStat.ATTACK, 2);

        player.deck.drawPile.add(attackCard);

        boolean queued = combat.queueAction(
                new CombatAction(
                        player,
                        attackCard,
                        combat.getEnemies().getFirst()
                )
        );

        check("Queue rejected before combat starts", !queued);
    }

    private static void testUndoSpeed() {

        PlayerCharacter player = createPlayer();
        Combat combat = createCombat(player);

        ActionCard speedCard =
                createCard("Quick Thinking", Card.CardStat.SPEED, 2);

        player.deck.drawPile.add(speedCard);

        combat.startCombat();

        combat.queueAction(
                new CombatAction(
                        player,
                        speedCard,
                        combat.getEnemies().getFirst()
                )
        );

        check(
                "Speed increases before undo",
                combat.getAvailableActions(player) == 5
        );

        check(
                "Undo Speed succeeds",
                combat.undoLastAction()
        );

        check(
                "Speed returns to original value",
                combat.getAvailableActions(player) == 3
        );
    }

    private static void testUndoStrength() {

        PlayerCharacter player = createPlayer();
        Combat combat = createCombat(player);

        ActionCard strengthCard =
                createCard("Battle Focus", Card.CardStat.STRENGTH, 2);

        player.deck.drawPile.add(strengthCard);

        for (int i = 0; i < 6; i++) {
            player.deck.drawPile.add(
                    createCard("Attack " + i, Card.CardStat.ATTACK, 1)
            );
        }

        combat.startCombat();

        int originalHandSize = player.deck.hand.size();
        int originalDrawPileSize = player.deck.drawPile.size();

        combat.queueAction(
                new CombatAction(
                        player,
                        strengthCard,
                        combat.getEnemies().getFirst()
                )
        );

        check(
                "Strength draws before undo",
                player.deck.hand.size() == originalHandSize + 2
        );

        check(
                "Undo Strength succeeds",
                combat.undoLastAction()
        );

        check(
                "Hand restored after undo",
                player.deck.hand.size() == originalHandSize
        );

        check(
                "Draw pile restored after undo",
                player.deck.drawPile.size() == originalDrawPileSize
        );
    }
}
