package org.example.card;

import com.google.gson.annotations.SerializedName;
import org.example.Character.PlayerCharacter;

public abstract class Card {
    /**
     * The card's name or title.
     */
    String name;

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

    /**
     * Detects if card should be destroyed after being played
     * */
    boolean destroyOnPlay = false;

    public Card(String name, String description, CardStat stat, int bonus, int drawCardsAmount) {
        this.name = name;
        this.description = description;
        this.stat = stat;
        this.bonus = bonus;
        this.drawCardsAmount = drawCardsAmount;
    }

    abstract void applyDrawEffect(PlayerCharacter character);
    abstract void applyDiscardEffect(PlayerCharacter character);

    abstract void destroyCard();
    abstract CardType getType();

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
