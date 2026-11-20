package org.example.GameLoop;

import org.example.Character.PlayerCharacter;
import org.example.card.Card;
import org.example.enemies.Enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import org.example.card.ActionCard;

public class Combat {
    private List<PlayerCharacter> characters = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private int turnCount = 0;
    private CombatState state = CombatState.NOT_STARTED;
    private List<CombatAction> queuedActions = new ArrayList<>();
    private final Map<PlayerCharacter, Integer> temporaryActions =
            new HashMap<>();
    private final Map<CombatAction, List<Card>> planningDraws =
            new HashMap<>();
    private final List<CombatEvent> lastTurnEvents = new ArrayList<>();

    public Combat(){}

    public void addCharacter(PlayerCharacter player){
        this.characters.add(player);
    }

    public void addEnemy(Enemy enemy){
        this.enemies.add(enemy);
    }

    public void startCombat() {

        if (state != CombatState.NOT_STARTED) {
            return;
        }

        turnCount = 1;
        queuedActions.clear();

        for (PlayerCharacter player : characters) {
            player.deck.fillHand();
        }

        state = CombatState.PLAYER_TURN;

        System.out.println("Combat started! Turn " + turnCount);
    }

    public boolean queueAction(CombatAction action) {

        if (action == null) {
            return false;
        }

        if (state != CombatState.PLAYER_TURN) {
            return false;
        }

        PlayerCharacter player = action.getPlayer();
        Card card = action.getCard();

        if (player == null || card == null) {
            return false;
        }

        if (!characters.contains(player)) {
            return false;
        }

        if (!player.isAlive()) {
            return false;
        }

        // Make sure the card is in the player's hand
        if (!player.deck.hand.contains(card)) {
            return false;
        }

        int actionsQueued = 0;
        int cardCopiesQueued = 0;

        for (CombatAction queued : queuedActions) {

            if (queued.getPlayer() == player) {
                actionsQueued++;
            }

            // Compare object identity, not just card names
            if (queued.getPlayer() == player
                    && queued.getCard() == card) {
                cardCopiesQueued++;
            }
        }

        if (actionsQueued >= getAvailableActions(player)) {
            return false;
        }

        // Count available references to this exact card object
        int cardCopiesInHand = 0;

        for (Card handCard : player.deck.hand) {
            if (handCard == card) {
                cardCopiesInHand++;
            }
        }

        if (cardCopiesQueued >= cardCopiesInHand) {
            return false;
        }

        queuedActions.add(action);

        if (card instanceof ActionCard actionCard
                && actionCard.getStat() == Card.CardStat.STRENGTH) {

            List<Card> drawnCards = new ArrayList<>();

            for (int i = 0; i < actionCard.getBonus(); i++) {
                Card drawn = player.deck.drawCard();

                if (drawn == null) {
                    break;
                }

                drawnCards.add(drawn);
            }

            planningDraws.put(action, drawnCards);

        } else {
            card.applyPlanningEffect(player, this);
        }

        return true;
    }

    public void addTemporaryActions(
            PlayerCharacter player,
            int amount
    ) {
        if (state != CombatState.PLAYER_TURN) {
            return;
        }

        temporaryActions.merge(player, amount, Integer::sum);
    }

    public int getAvailableActions(PlayerCharacter player) {
        return Math.max(
                0,
                player.getStat("SPEED")
                        + temporaryActions.getOrDefault(player, 0)
        );
    }

    public int getQueuedActionCount(PlayerCharacter player) {
        int count = 0;

        for (CombatAction action : queuedActions) {
            if (action.getPlayer() == player) {
                count++;
            }
        }

        return count;
    }

    private int calculateAttackDamage(PlayerCharacter player) {

        int totalDamage = player.getStat("ATTACK");

        for (CombatAction action : queuedActions) {

            if (action.getPlayer() != player) {
                continue;
            }

            Card card = action.getCard();

            if (card.getStat() == Card.CardStat.ATTACK) {
                totalDamage += card.getBonus();
            }
        }

        return Math.max(0, totalDamage);
    }

    public boolean removeQueuedAction(int index) {
        // TODO: Implement safe queue removal and effect rollback.
        return false;
    }

    public void clearQueuedActions() {
        // TODO: Implement safe queue clearing and effect rollback.
    }

//    TODO: Reimplement after bugs with removing strength and speed are cleared
//    public boolean removeQueuedAction(int index) {
//
//        if (state != CombatState.PLAYER_TURN) {
//            return false;
//        }
//
//        if (index < 0 || index >= queuedActions.size()) {
//            return false;
//        }
//
//        queuedActions.remove(index);
//        return true;
//    }
//
//    public void clearQueuedActions() {
//
//        if (state == CombatState.PLAYER_TURN) {
//            queuedActions.clear();
//        }
//    }

    public void recordPlanningDraw(CombatAction action, Card card) {
        planningDraws
                .computeIfAbsent(action, key -> new ArrayList<>())
                .add(card);
    }

    private void resolveEnemyAttacks() {

        Random rand = new Random();

        for (Enemy enemy : enemies) {

            if (enemy.getHealth() <= 0) {
                continue;
            }

            for (int i = 0; i < enemy.getAttacks(); i++) {

                List<PlayerCharacter> livingPlayers =
                        new ArrayList<>();

                for (PlayerCharacter player : characters) {
                    if (player.isAlive()) {
                        livingPlayers.add(player);
                    }
                }

                if (livingPlayers.isEmpty()) {
                    return;
                }

                PlayerCharacter target =
                        livingPlayers.get(rand.nextInt(livingPlayers.size()));

                target.takeDamage(enemy.getDamage());

                lastTurnEvents.add(
                        new CombatEvent(
                                CombatEventType.ENEMY_ATTACK,
                                enemy,
                                target,
                                enemy.getDamage()
                        )
                );

                if (!target.isAlive()) {
                    lastTurnEvents.add(
                            new CombatEvent(
                                    CombatEventType.DEATH,
                                    enemy,
                                    target,
                                    0
                            )
                    );
                }

                System.out.println(
                        "Enemy attacks " + target.name
                                + " for " + enemy.getDamage()
                                + " damage. HP: "
                                + target.getCurrentHealth()
                );
            }
        }
    }


    public void endTurn() {

        // Only end a turn during the planning phase.
        if (state != CombatState.PLAYER_TURN) {
            return;
        }

        state = CombatState.RESOLVING;
        lastTurnEvents.clear();

        System.out.println(
                "Resolving turn " + turnCount + "..."
        );

        // Each character performs one combined attack.
        for (PlayerCharacter player : characters) {

            if (!player.isAlive()) {
                continue;
            }

            // Calculate attack damage before consuming cards.
            int totalDamage = calculateAttackDamage(player);

            // Find the first living target selected by this player.
            Enemy attackTarget = null;

            for (CombatAction action : queuedActions) {
                if (action.getPlayer() == player
                        && action.getTarget() != null
                        && action.getTarget().getHealth() > 0) {

                    attackTarget = action.getTarget();
                    break;
                }
            }

            // Execute the player's queued cards.
            for (CombatAction action : queuedActions) {

                if (action.getPlayer() != player) {
                    continue;
                }

                Card card = action.getCard();

                int healthBefore = player.getCurrentHealth();

                boolean played = player.deck.play(card, player);

                int healthAfter = player.getCurrentHealth();

                if (played && healthAfter > healthBefore) {
                    lastTurnEvents.add(
                            new CombatEvent(
                                    CombatEventType.HEAL,
                                    player,
                                    player,
                                    healthAfter - healthBefore
                            )
                    );
                }

                if (!played) {
                    System.out.println(
                            "Unable to play card: " + card.getName()
                    );
                }
            }

            // Apply the combined attack.
            if (attackTarget != null
                    && attackTarget.getHealth() > 0) {

                attackTarget.giveDamage(totalDamage);

                lastTurnEvents.add(
                        new CombatEvent(
                                CombatEventType.PLAYER_ATTACK,
                                player,
                                attackTarget,
                                totalDamage
                        )
                );

                if (attackTarget.getHealth() <= 0) {
                    lastTurnEvents.add(
                            new CombatEvent(
                                    CombatEventType.DEATH,
                                    player,
                                    attackTarget,
                                    0
                            )
                    );
                }

                System.out.println(
                        player.name + " attacks for "
                                + totalDamage + " damage!"
                );
            }
        }

        // Clear the planning data.
        queuedActions.clear();
        temporaryActions.clear();
        planningDraws.clear();

        // Check whether the players won.
        if (allEnemiesDefeated()) {
            state = CombatState.VICTORY;

            lastTurnEvents.add(
                    new CombatEvent(
                            CombatEventType.VICTORY,
                            null,
                            null,
                            0
                    )
            );

            System.out.println("Victory!");
            return;
        }

        // Surviving enemies attack.
        state = CombatState.ENEMY_TURN;
        resolveEnemyAttacks();

        // Check whether the players lost.
        if (allPlayersDefeated()) {
            state = CombatState.DEFEAT;

            lastTurnEvents.add(
                    new CombatEvent(
                            CombatEventType.DEFEAT,
                            null,
                            null,
                            0
                    )
            );

            System.out.println("Defeat!");
            return;
        }

        // Check whether players have too many cards.
        if (needsDiscarding()) {

            state = CombatState.DISCARDING;

            System.out.println(
                    "Players must discard down to their hand limits."
            );

            return;
        }

// If no discarding is necessary, begin the next turn.
        startNextTurn();
    }


    public boolean undoLastAction() {

        if (state != CombatState.PLAYER_TURN
                || queuedActions.isEmpty()) {
            return false;
        }

        CombatAction lastAction =
                queuedActions.getLast();

        Card card = lastAction.getCard();
        PlayerCharacter player = lastAction.getPlayer();

        // Reverse Strength draws first
        List<Card> drawnCards = planningDraws.remove(lastAction);

        if (drawnCards != null) {
            for (int i = drawnCards.size() - 1; i >= 0; i--) {
                player.deck.undoDraw(drawnCards.get(i));
            }
        }

        // Reverse Speed bonuses
        if (card instanceof ActionCard actionCard
                && actionCard.getStat() == Card.CardStat.SPEED) {

            addTemporaryActions(player, -actionCard.getBonus());
        }

        queuedActions.removeLast();

        return true;
    }

    private boolean allEnemiesDefeated() {
        for (Enemy enemy : enemies) {
            if (enemy.getHealth() > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean allPlayersDefeated() {
        for (PlayerCharacter player : characters) {
            if (player.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private boolean needsDiscarding() {

        for (PlayerCharacter player : characters) {

            if (player.isAlive()
                    && player.deck.hand.size() >
                    Math.max(0, player.getStat("STRENGTH"))) {
                return true;
            }
        }

        return false;
    }

    public boolean discardCard(PlayerCharacter player, Card card) {

        // Only allow discarding during the discard phase.
        if (state != CombatState.DISCARDING) {
            return false;
        }

        // Make sure this character belongs to the combat.
        if (player == null || card == null
                || !characters.contains(player)
                || !player.isAlive()) {
            return false;
        }

        // Don't allow unnecessary discards.
        if (player.deck.hand.size()
                <= Math.max(0, player.getStat("STRENGTH"))) {
            return false;
        }

        // Discard the selected card.
        if (!player.deck.discardFromHand(card)) {
            return false;
        }

        // Check whether everyone has finished discarding.
        if (!needsDiscarding()) {
            startNextTurn();
        }

        return true;
    }

    private void startNextTurn() {

        turnCount++;

        for (PlayerCharacter player : characters) {

            if (player.isAlive()) {
                player.deck.fillHand();
            }
        }

        state = CombatState.PLAYER_TURN;

        System.out.println("Starting turn " + turnCount);
    }

    // Returns how many cards a character must discard.
    public int getRequiredDiscards(PlayerCharacter player) {

        if (state != CombatState.DISCARDING) {
            return 0;
        }

        if (player == null || !characters.contains(player)
                || !player.isAlive()) {
            return 0;
        }

        return Math.max(
                0,
                player.deck.hand.size()
                        - Math.max(0, player.getStat("STRENGTH"))
        );
    }

    // ----- Getters ----- //
    public CombatState getState() {
        return state;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public List<CombatAction> getQueuedActions() {
        return List.copyOf(queuedActions);
    }

    public List<Enemy> getEnemies() {
        return List.copyOf(enemies);
    }

    public List<PlayerCharacter> getCharacters() {
        return List.copyOf(characters);
    }

    public List<CombatEvent> getLastTurnEvents() {
        return List.copyOf(lastTurnEvents);
    }
}
