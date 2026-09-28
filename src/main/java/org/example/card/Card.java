package org.example.card;

public abstract class Card {
    /**
     * The card's name or title.
     */
    String name;

    /**
     * All cards have a type, which can be:
     * <li>Ability (permanent bonus from character creation)</li>
     * <li>Loot (one time use found in fights or occasionally the store)</li>
     * <li>Action (last the duration of the run, bought from store)</li>
     */
    CardType type;

    /**
     * All cards have a short instructional message that describes the function.
     */
    String description;

    /**
     * Stat can be:
     * <li>Health (number of health points)</li>
     * <li>Strength (number of cards in hand)</li>
     * <li>Speed (number of cards played per turn)</li>
     * <li>Attack (base damage)</li>
     */
    CardStat stat;

    /**
     * Bonus is the amount to add to a specific stat when the card is played.
     */
    int bonus;

    /**
     * Only applicable if stat is "Strength", allows an amount of cards to be drawn.
     */
    int drawCardsAmount;

    public Card(String name, CardType type, String description, CardStat stat, int bonus, int drawCardsAmount) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.stat = stat;
        this.bonus = bonus;
        this.drawCardsAmount = drawCardsAmount;
    }

    abstract void applyDrawEffect();

    public enum CardType {
        ABILITY,
        LOOT,
        ACTION
    }

    public enum CardStat {
        HEALTH,
        STRENGTH,
        SPEED,
        ATTACK
    }
}
