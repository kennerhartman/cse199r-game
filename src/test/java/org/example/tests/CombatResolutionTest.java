package org.example.tests;

import org.example.Character.PlayerCharacter;
import org.example.GameLoop.*;
import org.example.Player.Player;
import org.example.card.ActionCard;
import org.example.card.Card;
import org.example.enemies.Enemy;
import org.example.map.MapNode;

public class CombatResolutionTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println("=== COMBAT RESOLUTION TESTS ===");

        testAttackDamage();
        testHealing();
        testCardConsumption();
        testEnemyAttack();
        testVictory();
        testDefeat();
        testHandRetention();
        testTurnProgression();
        testCombatEvents();

        System.out.println("\n=== TEST RESULTS ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                    failed + " combat resolution tests failed."
            );
        }
    }

    // ----- Test Helpers -----

    private static void check(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + description);
        } else {
            failed++;
            System.out.println("[FAIL] " + description);
        }
    }

    private static PlayerCharacter createPlayer() {
        return new PlayerCharacter(new Player(), "Test Knight");
    }

    private static ActionCard createCard(
            String name,
            Card.CardStat stat,
            int bonus
    ) {
        return new ActionCard(
                name,
                "Combat test card",
                stat,
                bonus,
                0
        );
    }

    private static Combat createCombat(
            PlayerCharacter player,
            Enemy enemy
    ) {
        Combat combat = new Combat();
        combat.addCharacter(player);
        combat.addEnemy(enemy);
        return combat;
    }

    private static Enemy createEnemy(PlayerCharacter player) {
        return new Enemy(player, MapNode.NodeType.COMBAT);
    }

    // ----- TEST 1: Attack Damage -----

    private static void testAttackDamage() {

        PlayerCharacter player = createPlayer();
        Enemy enemy = createEnemy(player);
        Combat combat = createCombat(player, enemy);

        ActionCard attackCard =
                createCard("Power Strike", Card.CardStat.ATTACK, 3);

        player.deck.drawPile.add(attackCard);

        combat.startCombat();

        int enemyHealthBefore = enemy.getHealth();
        int expectedDamage = player.getStat("ATTACK") + 3;

        boolean queued = combat.queueAction(
                new CombatAction(player, attackCard, enemy)
        );

        combat.endTurn();

        check("Attack card queued", queued);

        check(
                "Attack deals base damage plus bonus",
                enemy.getHealth() ==
                        Math.max(0, enemyHealthBefore - expectedDamage)
        );
    }

    // ----- TEST 2: Healing -----

    private static void testHealing() {

        PlayerCharacter player = createPlayer();
        player.takeDamage(6);

        Enemy enemy = createEnemy(player);
        Combat combat = createCombat(player, enemy);

        ActionCard healCard =
                createCard("Healing Potion", Card.CardStat.HEALTH, 4);

        player.deck.drawPile.add(healCard);

        combat.startCombat();

        int healthBefore = player.getCurrentHealth();

        combat.queueAction(
                new CombatAction(player, healCard, enemy)
        );

        combat.endTurn();

        // The enemy also attacks during this turn,
        // so subtract the expected enemy damage.
        int expectedHealth = Math.max(
                0,
                Math.min(player.getMaxHealth(), healthBefore + 4)
                        - enemy.getDamage() * enemy.getAttacks()
        );

        check(
                "Healing card restores health",
                player.getCurrentHealth() == expectedHealth
        );
    }

    // ----- TEST 3: Card Consumption -----



    private static void testCardConsumption() {

        PlayerCharacter player = createPlayer();
        Enemy enemy = createEnemy(player);
        Combat combat = createCombat(player, enemy);

        ActionCard attackCard =
                createCard("Attack", Card.CardStat.ATTACK, 1);

        // Add the card we're testing.
        player.deck.drawPile.add(attackCard);

        // Add enough other cards so that the discard pile
        // won't need to be reshuffled on the next turn.
        for (int i = 0; i < 10; i++) {
            player.deck.drawPile.add(
                    createCard(
                            "Filler " + i,
                            Card.CardStat.HEALTH,
                            1
                    )
            );
        }

        combat.startCombat();

        // Confirm the card is initially in the hand.
        check(
                "Attack card starts in hand",
                player.deck.hand.contains(attackCard)
        );

        boolean queued = combat.queueAction(
                new CombatAction(player, attackCard, enemy)
        );

        check("Attack card queued", queued);

        combat.endTurn();

        // Because the draw pile still contains filler cards,
        // the played Attack card should remain discarded.
        check(
                "Played card removed from hand",
                !player.deck.hand.contains(attackCard)
        );

        check(
                "Played card moved to discard",
                player.deck.discard.contains(attackCard)
        );
    }



    // ----- TEST 4: Enemy Attacks -----

    private static void testEnemyAttack() {

        PlayerCharacter player = createPlayer();
        Enemy enemy = createEnemy(player);
        Combat combat = createCombat(player, enemy);

        int startingHealth = player.getCurrentHealth();

        combat.startCombat();

        // Queue nothing. Enemy should still attack.
        combat.endTurn();

        int expectedHealth = Math.max(
                0,
                startingHealth - enemy.getDamage() * enemy.getAttacks()
        );

        check(
                "Enemy attacks when player ends turn",
                player.getCurrentHealth() == expectedHealth
        );
    }

    // ----- TEST 5: Victory -----

    private static void testVictory() {

        PlayerCharacter player = createPlayer();
        Enemy enemy = createEnemy(player);
        Combat combat = createCombat(player, enemy);

        ActionCard attackCard =
                createCard("Finishing Blow", Card.CardStat.ATTACK, 100);

        player.deck.drawPile.add(attackCard);

        combat.startCombat();

        combat.queueAction(
                new CombatAction(player, attackCard, enemy)
        );

        combat.endTurn();

        check(
                "Enemy defeated",
                enemy.getHealth() <= 0
        );

        check(
                "Combat enters VICTORY state",
                combat.getState() == CombatState.VICTORY
        );
    }

    // ----- TEST 6: Defeat -----

    private static void testDefeat() {

        PlayerCharacter player = createPlayer();
        Enemy enemy = createEnemy(player);
        Combat combat = createCombat(player, enemy);

        // Leave the player with 1 HP.
        player.takeDamage(player.getCurrentHealth() - 1);

        combat.startCombat();

        // No attack cards queued.
        combat.endTurn();

        check(
                "Player defeated",
                !player.isAlive()
        );

        check(
                "Combat enters DEFEAT state",
                combat.getState() == CombatState.DEFEAT
        );
    }

    // ----- TEST 7: Hand Retention -----

    private static void testHandRetention() {

        PlayerCharacter player = createPlayer();
        Enemy enemy = createEnemy(player);
        Combat combat = createCombat(player, enemy);

        ActionCard savedCard =
                createCard("Saved Card", Card.CardStat.ATTACK, 1);

        // Add enough cards to fill the hand.
        player.deck.drawPile.add(savedCard);

        for (int i = 0; i < 4; i++) {
            player.deck.drawPile.add(
                    createCard("Extra " + i, Card.CardStat.HEALTH, 1)
            );
        }

        combat.startCombat();

        combat.endTurn();

        check(
                "Unplayed card remains in hand",
                player.deck.hand.contains(savedCard)
        );
    }

    // ----- TEST 8: Turn Progression -----

    private static void testTurnProgression() {

        PlayerCharacter player = createPlayer();
        Enemy enemy = createEnemy(player);
        Combat combat = createCombat(player, enemy);

        combat.startCombat();

        check(
                "Combat begins on turn 1",
                combat.getTurnCount() == 1
        );

        combat.endTurn();

        check(
                "Turn count increments",
                combat.getTurnCount() == 2
        );

        check(
                "Combat returns to PLAYER_TURN",
                combat.getState() == CombatState.PLAYER_TURN
        );
    }

    private static void testCombatEvents() {

        PlayerCharacter player = createPlayer();
        Enemy enemy = createEnemy(player);
        Combat combat = createCombat(player, enemy);

        ActionCard attackCard =
                createCard("Attack", Card.CardStat.ATTACK, 3);

        player.deck.drawPile.add(attackCard);

        combat.startCombat();

        combat.queueAction(
                new CombatAction(player, attackCard, enemy)
        );

        combat.endTurn();

        boolean playerAttackFound = false;
        boolean enemyAttackFound = false;

        for (CombatEvent event : combat.getLastTurnEvents()) {

            if (event.getType() == CombatEventType.PLAYER_ATTACK) {
                playerAttackFound = true;
            }

            if (event.getType() == CombatEventType.ENEMY_ATTACK) {
                enemyAttackFound = true;
            }
        }

        check("Player attack event recorded", playerAttackFound);
        check("Enemy attack event recorded", enemyAttackFound);
    }
}
