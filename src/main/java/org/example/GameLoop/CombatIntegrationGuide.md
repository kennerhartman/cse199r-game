### Note For Kenner:
*Combat resolves immediately upon calling endTurn(), deals all damage healing etc. GUI can read getLastTurnEvents() for the animations. There is currently an ANIMATION combat state but it isn't used. Also combat currently selects the first available enemy, which we might want to eventually change to target a selected enemy, but for now it should be fine. IMPORTANT: SHOPS DON'T WORK RIGHT NOW. They never set encounter to completed so you won't be able to progress after loading a shop. (also this branch is so incredibly vibe coded, and the guide is written by AI as well. Everything seems to pass the tests though.) Also: combat even list is the thing that you're gonna want to use to bridge, cuz it contains the order that things need to be animated in.*

# Hero of the Draw — Combat & Encounter GUI Integration Guide

## Overview

This guide explains how to connect Hero of the Draw's existing Java game logic to the Raylib GUI.

The core combat system has been implemented and tested independently of the GUI. It supports:

- Selecting map nodes and creating encounters
- Starting combat encounters
- Drawing cards and maintaining a hand
- Queueing cards before ending a turn
- Applying Strength and Speed bonuses during planning
- Resolving Attack and Health effects
- Enemy attacks
- Victory and defeat detection
- Discarding excess cards
- Recording combat events for animations

**Important:** The GUI should handle displaying information, receiving player input, and playing animations. It should not independently calculate combat damage, change health, or move cards between piles.

---

## 1. Project Architecture

The main classes involved are:

| Class | Package | Responsibility |
|---|---|---|
| `MapManager` | `org.example.map` | Map navigation, available nodes, encounter creation |
| `Encounter` | `org.example.map` | Starts encounters and checks completion |
| `Combat` | `org.example.GameLoop` | Manages turns, actions, damage, and combat states |
| `CombatAction` | `org.example.GameLoop` | Stores a queued card, player, and enemy target |
| `CombatState` | `org.example.GameLoop` | Represents the current combat phase |
| `CombatEvent` | `org.example.GameLoop` | Stores information about resolved combat events |
| `CombatEventType` | `org.example.GameLoop` | Identifies event types |
| `PlayerCharacter` | `org.example.Character` | Character stats, current health, and deck |
| `Deck` | `org.example.card` | Drawing, playing, discarding, and destroying cards |
| `Card` | `org.example.card` | Base class for Ability, Action, and Loot cards |
| `Enemy` | `org.example.enemies` | Enemy health, damage, attacks, and rewards |

### General flow

```text
Player selects a map node
          |
          v
MapManager.selectNode()
          |
          v
Encounter is created
          |
          v
Encounter.startEncounter()
          |
          v
Combat is initialized
          |
          v
PLAYER_TURN
          |
          v
Player queues cards
          |
          v
Combat.endTurn()
          |
          v
Actions and enemy attacks resolve
          |
          v
Combat events become available
          |
          v
DISCARDING / PLAYER_TURN / VICTORY / DEFEAT
```

The GUI should maintain references to the active `MapManager`, `Encounter`, and `Combat` objects.

---

## 2. Starting an Encounter

### Selecting a map node

When a player clicks a node on the map:

```java
mapManager.selectNode(selectedNode, playerCharacter);
```

`selectedNode` must be an actual node from:

```java
mapManager.getActiveChoices();
```

`MapManager` validates the selection, updates the current node, and creates an `Encounter`.

It also prevents selecting another node while an unfinished encounter is active.

**Note:** `selectNode()` currently returns `void`. To determine whether selection succeeded, check the resulting current node or encounter.

### Retrieving the encounter

```java
Encounter encounter = mapManager.getCurrentEncounter();
```

Then start it:

```java
if (encounter != null && !encounter.hasStarted()) {
    encounter.startEncounter();
}
```

### Retrieving combat

For combat and boss encounters:

```java
Combat combat = encounter.getCombat();
```

`startEncounter()` creates the `Combat`, adds the character and enemy, and calls `startCombat()`.

At this point, combat should be in `PLAYER_TURN`.

**Important:** `getCombat()` returns `null` before combat initialization or for non-combat encounters.

---

## 3. Combat States

Combat uses the following enum:

```java
public enum CombatState {
    NOT_STARTED,
    PLAYER_TURN,
    RESOLVING,
    ENEMY_TURN,
    DISCARDING,
    ANIMATING,
    VICTORY,
    DEFEAT
}
```

Retrieve the current state with:

```java
CombatState state = combat.getState();
```

| State | GUI behavior |
|---|---|
| `NOT_STARTED` | Wait for combat initialization |
| `PLAYER_TURN` | Display hand, queue, targets, and End Turn button |
| `RESOLVING` | Combat logic is processing actions |
| `ENEMY_TURN` | Enemy actions are being processed |
| `DISCARDING` | Ask the player to discard excess cards |
| `ANIMATING` | Reserved for future animation synchronization |
| `VICTORY` | Display victory and allow map progression |
| `DEFEAT` | Display defeat or game-over screen |

### Important implementation detail

`endTurn()` currently resolves combat **synchronously**.

This means `RESOLVING` and `ENEMY_TURN` occur during the method call and are generally not observable between GUI frames.

The `ANIMATING` state is not currently entered by combat logic.

The GUI should retrieve the resulting events immediately after `endTurn()` and animate them separately.

---

## 4. Displaying Combat Information

### Characters

```java
List<PlayerCharacter> characters = combat.getCharacters();
```

For each character:

```java
int currentHealth = player.getCurrentHealth();
int maxHealth = player.getMaxHealth();

int attack = player.getStat("ATTACK");
int speed = player.getStat("SPEED");
int strength = player.getStat("STRENGTH");
```

`currentHealth` represents actual remaining HP.

`getStat("HEALTH")` and `getMaxHealth()` represent maximum HP.

### Enemies

```java
List<Enemy> enemies = combat.getEnemies();
```

For each enemy:

```java
int health = enemy.getHealth();
int damage = enemy.getDamage();
int attacks = enemy.getAttacks();
```

An enemy is defeated when its health reaches zero.

### Player hand

Currently:

```java
List<Card> hand = player.deck.hand;
```

To display each card:

```java
for (Card card : hand) {
    String name = card.getName();
    String description = card.getDescription();
    Card.CardStat stat = card.getStat();
    int bonus = card.getBonus();
}
```

The hand list is currently public. The GUI should treat it as read-only.

**Do not directly add or remove cards from the hand.** Doing so can bypass AbilityCard passive effects.

---

## 5. Queueing Cards

Players select cards during `PLAYER_TURN`.

Cards are queued and then resolved together when the player ends their turn.

### Creating an action

```java
CombatAction action = new CombatAction(
    playerCharacter,
    selectedCard,
    selectedEnemy
);
```

The action stores:

- The character playing the card
- The selected card
- The selected enemy target

### Adding an action

```java
boolean success = combat.queueAction(action);
```

If `success` is `false`, the action was rejected.

Possible reasons include:

- Combat is not in `PLAYER_TURN`
- The player is defeated
- The card is not in the player's hand
- The card is already queued beyond its available copies
- The player has reached their action limit

### Available action slots

```java
int available = combat.getAvailableActions(playerCharacter);

int queued = combat.getQueuedActionCount(playerCharacter);
```

Example GUI display:

```text
Actions: 2 / 5
```

`getAvailableActions()` returns the total number of actions currently permitted, including temporary Speed bonuses.

### Viewing the queue

```java
List<CombatAction> actions = combat.getQueuedActions();
```

This returns a read-only copy of the current action queue.

The GUI can use this to display the cards the player has selected.

### Undoing an action

```java
boolean undone = combat.undoLastAction();
```

This removes the most recently queued action and reverses its supported planning effects.

**Important:** Only `undoLastAction()` is currently functional.

`removeQueuedAction(index)` and `clearQueuedActions()` are placeholders and should not be used.

---

## 6. Card Behavior

Hero of the Draw currently has three card categories.

### AbilityCards

AbilityCards have two main effects:

1. During character creation, they can set a character's base stat.
2. While held in the player's hand, they provide passive stat bonuses.

Their passive effects activate when drawn and are removed when they leave the hand.

The GUI should not apply or remove these bonuses manually.

### ActionCards

ActionCards have no passive draw effect.

Their behavior depends on the affected stat:

| Stat | Behavior |
|---|---|
| `ATTACK` | Adds to the character's combined attack damage |
| `HEALTH` | Restores current health during resolution |
| `STRENGTH` | Draws extra cards immediately when queued |
| `SPEED` | Increases available action slots immediately when queued |

Strength and Speed activate during planning.

Attack and Health resolve when the player ends their turn.

### LootCards

LootCards are intended to be more powerful, single-use cards.

Their advanced effects, including possible cursed or blessed behavior, are not fully implemented yet.

The GUI can display them, but should not assume that all intended LootCard mechanics are functional.

---

## 7. Strength and Speed During Planning

### Strength

When a Strength ActionCard is queued:

```java
combat.queueAction(action);
```

The additional cards are drawn immediately.

The GUI should refresh the displayed hand after a successful queue operation.

The newly drawn cards can be selected during the same planning phase, provided the player has available action slots.

### Speed

When a Speed ActionCard is queued, it immediately increases the number of permitted actions.

For example:

```text
Base SPEED: 3
Speed card: +2
Available actions: 5
```

The Speed card itself occupies one action slot.

The GUI should refresh the action counter after queueing or undoing a card.

### Undo behavior

Undoing Strength returns its extra drawn cards to the draw pile.

Reshuffles triggered during planning are intentionally not reversed. This is a deliberate deck-manipulation mechanic.

The GUI should use `undoLastAction()` rather than attempting to restore deck contents itself.

---

## 8. Ending the Player's Turn

When the player presses End Turn:

```java
if (combat.getState() == CombatState.PLAYER_TURN) {
    combat.endTurn();
}
```

The combat system then:

1. Calculates combined attack damage.
2. Executes queued card effects.
3. Applies the player's combined attack.
4. Checks for victory.
5. Processes surviving enemy attacks.
6. Checks for defeat.
7. Determines whether the player needs to discard.
8. Starts the next turn if appropriate.

### Combined attack

Each character performs one combined attack per turn.

```text
Base ATTACK: 5
Attack card: +3
Attack card: +4

Total damage: 12
```

Currently, the target is the first living enemy referenced by that player's queued actions.

**Targeting limitation:** Even non-attack cards can supply the selected target. For now, the GUI should pass a consistent intended enemy target when constructing actions.

### Refreshing the GUI

After calling `endTurn()`:

```java
combat.endTurn();

List<CombatEvent> events = combat.getLastTurnEvents();
CombatState nextState = combat.getState();
```

The GUI should process the events and then display the appropriate next phase.

---

## 9. Handling the Discard Phase

Strength cards can cause a player to exceed their normal hand limit.

After turn resolution, Combat may enter:

```java
CombatState.DISCARDING
```

The player must then choose cards to discard.

### Checking how many cards must be discarded

```java
int required = combat.getRequiredDiscards(playerCharacter);
```

Example:

```text
Choose 2 cards to discard.
```

### Discarding a selected card

```java
boolean success = combat.discardCard(
    playerCharacter,
    selectedCard
);
```

If successful, the card is removed from the hand and moved to the discard pile.

The system automatically removes any applicable passive hand bonus.

### Completing the discard phase

Once all living characters have discarded down to their limits, Combat automatically starts the next turn.

The GUI does not need to call another method to advance combat.

### Important behavior

Discarding an unplayed LootCard does not destroy it.

Playing a LootCard may destroy it, depending on its configuration.

---

## 10. Combat Events and Animations

Combat records the events that occur during turn resolution.

Retrieve them with:

```java
List<CombatEvent> events = combat.getLastTurnEvents();
```

The list preserves the order in which events were recorded.

### Available event types

```java
public enum CombatEventType {
    PLAYER_ATTACK,
    ENEMY_ATTACK,
    HEAL,
    DEATH,
    VICTORY,
    DEFEAT
}
```

Each event provides:

```java
event.getType();
event.getSource();
event.getTarget();
event.getAmount();
```

`source` and `target` are currently typed as `Object`, so the GUI may need to check their runtime types.

### Example event processing

```java
for (CombatEvent event : combat.getLastTurnEvents()) {

    switch (event.getType()) {

        case PLAYER_ATTACK -> {
            PlayerCharacter attacker =
                (PlayerCharacter) event.getSource();

            Enemy target =
                (Enemy) event.getTarget();

            int damage = event.getAmount();

            // Queue player attack animation.
            // Queue enemy hurt animation.
            // Display damage number.
        }

        case ENEMY_ATTACK -> {
            Enemy attacker =
                (Enemy) event.getSource();

            PlayerCharacter target =
                (PlayerCharacter) event.getTarget();

            // Queue enemy attack animation.
            // Queue player hurt animation.
        }

        case HEAL -> {
            PlayerCharacter target =
                (PlayerCharacter) event.getTarget();

            int amount = event.getAmount();

            // Display healing animation and number.
        }

        case DEATH -> {
            Object target = event.getTarget();

            // Queue the appropriate death animation.
        }

        case VICTORY -> {
            // Prepare victory presentation.
        }

        case DEFEAT -> {
            // Prepare defeat presentation.
        }
    }
}
```

### Suggested animation mapping

| Combat event | Suggested animation |
|---|---|
| `PLAYER_ATTACK` | Knight Attack01/02/03, enemy Hurt |
| `ENEMY_ATTACK` | Orc Attack01/02, knight Hurt |
| `HEAL` | Healing effect or floating green number |
| `DEATH` | Character or enemy Death |
| `VICTORY` | Victory UI |
| `DEFEAT` | Game-over UI |

The existing animation system is on another branch.

Its exact API should be used when the branches are integrated. Do not assume any particular animation queue method names from this guide.

### Important: Event timing

Combat damage and healing have already been applied by the time `getLastTurnEvents()` is called.

The events describe actions that occurred; they do not delay the underlying logic.

If the GUI needs health bars to change gradually during animations, it will need to maintain presentation values separately from the authoritative combat values.

The GUI should also avoid replaying the same event list on every frame. Process it once after each successful turn resolution.

---

## 11. Victory and Defeat

### Victory

After combat ends:

```java
if (combat.getState() == CombatState.VICTORY) {
    // Display victory screen.
}
```

The encounter can also report completion:

```java
if (encounter.isCompleted()) {
    // Allow progression to the next map node.
}
```

`Encounter.isCompleted()` returns `true` for combat and boss encounters after victory.

### Defeat

```java
if (combat.getState() == CombatState.DEFEAT) {
    // Display defeat or game-over screen.
}
```

Defeat does not count as successful encounter completion.

The GUI should not allow normal map progression after defeat.

### Returning to the map

Once the victory presentation and any animations have finished, the GUI can return to the map screen.

The next available nodes can be retrieved with:

```java
mapManager.getActiveChoices();
```

The player can then select another node.

---

## 12. Example GUI-Side Workflow

The following is illustrative integration code, not a complete Raylib screen implementation.

```java
// MAP SCREEN: Player selects a node.
mapManager.selectNode(selectedNode, playerCharacter);

Encounter encounter = mapManager.getCurrentEncounter();

if (encounter != null && !encounter.hasStarted()) {
    encounter.startEncounter();
}

// For combat encounters:
Combat combat = encounter.getCombat();

// COMBAT SCREEN: Player selects a card.
CombatAction action = new CombatAction(
    playerCharacter,
    selectedCard,
    selectedEnemy
);

boolean queued = combat.queueAction(action);

// Refresh hand and action counter if queued.
if (queued) {
    // Update card display.
    // Update action counter.
    // Update queued card display.
}

// COMBAT SCREEN: Player presses End Turn.
if (combat.getState() == CombatState.PLAYER_TURN) {

    combat.endTurn();

    // Process events once.
    List<CombatEvent> events =
        combat.getLastTurnEvents();

    // Feed events into animation queue.
    // Refresh combat state.
}

// DISCARD SCREEN:
if (combat.getState() == CombatState.DISCARDING) {

    int required =
        combat.getRequiredDiscards(playerCharacter);

    // Display discard selection UI.
    // When the player selects a card:
    // combat.discardCard(playerCharacter, selectedCard);
}

// END OF COMBAT:
if (combat.getState() == CombatState.VICTORY) {
    // Play victory presentation.
    // Return to map afterward.
} else if (combat.getState() == CombatState.DEFEAT) {
    // Display game-over screen.
}
```

The actual GUI should keep encounter selection, combat rendering, and player input handling in their appropriate screen classes.

---

## 13. Public Method Reference

| Method | Purpose |
|---|---|
| `mapManager.getActiveChoices()` | Retrieve selectable map nodes |
| `mapManager.selectNode(node, player)` | Select a node and create an encounter |
| `mapManager.getCurrentEncounter()` | Retrieve active encounter |
| `encounter.startEncounter()` | Initialize encounter |
| `encounter.hasStarted()` | Check whether encounter has started |
| `encounter.getCombat()` | Retrieve combat instance |
| `encounter.isCompleted()` | Check successful encounter completion |
| `combat.getState()` | Retrieve current combat phase |
| `combat.getCharacters()` | Retrieve combat characters |
| `combat.getEnemies()` | Retrieve enemies |
| `combat.getTurnCount()` | Retrieve current turn |
| `combat.getAvailableActions(player)` | Retrieve total permitted action slots |
| `combat.getQueuedActionCount(player)` | Retrieve number of queued actions |
| `combat.getQueuedActions()` | Retrieve queued actions |
| `combat.queueAction(action)` | Queue a selected card |
| `combat.undoLastAction()` | Undo most recently queued action |
| `combat.endTurn()` | Resolve the player's turn |
| `combat.getRequiredDiscards(player)` | Retrieve required discard count |
| `combat.discardCard(player, card)` | Discard a selected card during DISCARDING |
| `combat.getLastTurnEvents()` | Retrieve events from the most recent resolved turn |
| `player.getCurrentHealth()` | Retrieve current HP |
| `player.getMaxHealth()` | Retrieve maximum HP |
| `player.getStat(name)` | Retrieve effective character stat |

---

## 14. Known Limitations and Future Work

The following features are intentionally unfinished or simplified.

### Shops

Shop inventory generation exists, but the complete shop interaction and exit flow have not been implemented.

`Encounter.isCompleted()` currently returns `false` for shops.

**Avoid selecting shop nodes until shop completion is implemented**, or map progression will be blocked.

### Attack targeting

Each player performs one combined attack per turn.

The target is currently selected from queued actions rather than through a dedicated combat targeting API.

### Animation synchronization

Combat resolves immediately, and animation events are generated afterward.

The `ANIMATING` state is reserved but not yet integrated into the combat state machine.

### Queue editing

`undoLastAction()` works.

Arbitrary removal and clearing of queued actions are not implemented.

### LootCards

Advanced LootCard effects and cursed/blessed behavior are unfinished.

### Combat rewards

Enemy coin and loot-drop values exist, but the full reward distribution process is not yet connected to encounter completion.

### Multiple combatants

Combat supports lists of characters and enemies, but the current Encounter implementation creates one player character and one enemy.

### Combat event presentation

Combat events record actions in order, but do not provide full before-and-after state snapshots.

The GUI must handle animation presentation independently.

### Save/load behavior

Combat and encounter runtime state are not yet fully integrated with saving and loading. Avoid assuming that an encounter can be resumed in the middle of combat.

---

## 15. Testing

The logic has been tested independently of the GUI.

Existing tests include:

- `CombatPlanningTest`
- `CombatResolutionTest`
- `CombatDiscardTest`
- `EncounterIntegrationTest`

These verify planning effects, card resolution, combat results, discarding, and encounter creation.

When integrating the GUI, manually verify the following:

- [ ] Clicking a combat node creates the correct encounter.
- [ ] Combat starts and displays the player's hand.
- [ ] Queued cards appear in the correct order.
- [ ] Strength cards draw immediately.
- [ ] Speed cards increase available actions immediately.
- [ ] Undo Last Action updates the hand and action counter.
- [ ] End Turn resolves combat once.
- [ ] Attack and healing animations correspond to recorded events.
- [ ] Enemy attacks appear correctly.
- [ ] The discard phase appears when needed.
- [ ] Discarding enough cards starts the next turn.
- [ ] Victory returns the player to the map.
- [ ] Defeat displays the appropriate game-over screen.
- [ ] The player cannot skip an unfinished encounter.

---

## 16. Recommended Integration Order

To minimize debugging, connect the GUI in this order:

1. **Map navigation:** Node selection, encounter retrieval, and screen transitions.
2. **Combat display:** Characters, enemies, health bars, hand, and action counter.
3. **Card planning:** Selecting cards, choosing targets, queueing, and undo.
4. **Turn resolution:** End Turn button and state updates.
5. **Discarding:** Selecting cards to discard and continuing to the next turn.
6. **Combat events:** Translating event data into queued animations.
7. **Encounter completion:** Victory, defeat, and returning to the map.

Test each stage before moving to the next.

---

## Final Notes

The combat logic should remain the authoritative source of game state.

The GUI is responsible for showing that state and collecting player decisions.

In particular:

**Do not manually change health, calculate attack damage, or remove cards from the hand in GUI code.** Use the existing combat and deck methods.

For the first playable build, prioritize completing one full encounter from map selection through victory or defeat. Additional card effects, shops, rewards, and animation refinements can be added after that flow is working reliably.