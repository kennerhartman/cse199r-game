package org.example.card;

import org.example.serializer.Codec;
import org.example.serializer.RecordCodecBuilder;

public abstract class Card {
    public static final Codec<Card> CODEC = Codec.dispatch(
            "type",
            CardType.class,
            Card::getType,
            type -> switch (type) {
                case ABILITY -> AbilityCard.CODEC;
                case LOOT -> LootCard.CODEC;
                case ACTION -> ActionCard.CODEC;
            }
    );

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

    /**
     * Detects if card should be destroyed after being played
     * */
    boolean destroyOnPlay = false;

    public Card(String name, CardType type, String description, CardStat stat, int bonus, int drawCardsAmount, boolean destroyOnPlay) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.stat = stat;
        this.bonus = bonus;
        this.drawCardsAmount = drawCardsAmount;
        this.destroyOnPlay = destroyOnPlay;
    }

    public Card(String name, CardType type, String description, CardStat stat, int bonus, int drawCardsAmount) {
        this(name, type, description, stat, bonus, drawCardsAmount, false);
    }

    public String getName() {
        return this.name;
    }

    public CardType getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }

    public CardStat getStat() {
        return this.stat;
    }

    public int getBonus() {
        return this.bonus;
    }

    public int getDrawCardsAmount() {
        return this.drawCardsAmount;
    }

    public boolean isDestroyedOnPlay() {
        return this.destroyOnPlay;
    }

    abstract void applyDrawEffect();

    abstract void destroyCard();

    public static Codec<? extends Card> getCodec() {
        return AbilityCard.CODEC;
    }

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
