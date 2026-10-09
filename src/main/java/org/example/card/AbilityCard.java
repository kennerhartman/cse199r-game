package org.example.card;

import org.example.core.serializer.Codec;
import org.example.core.serializer.RecordCodecBuilder;

/**
 Ability cards are gained during character creation and last the duration of the character playthrough.
 <p>
 They have an instant effect during character creation which sets the base stat to a number, and a draw effect
 which is used during combat.
 */
public class AbilityCard extends Card {
    public static final Codec<AbilityCard> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Card::getName),
                    Codec.ofEnum(CardType.class).fieldOf("type").forGetter(Card::getType),
                    Codec.STRING.fieldOf("description").forGetter(Card::getDescription),
                    Codec.ofEnum(CardStat.class).fieldOf("stat").forGetter(Card::getStat),
                    Codec.INT.fieldOf("bonus").forGetter(Card::getBonus),
                    Codec.INT.fieldOf("draw_cards_amount").forGetter(Card::getDrawCardsAmount)
            ).apply(AbilityCard::new)
    );

    /**
     * The base value is Ability card specific, and is used to set the default stat value of a character on creation.
     */
    int base;

    public AbilityCard(String name, CardType type, String description, CardStat stat, int bonus, int drawCardsAmount) {
        super(name, type, description, stat, bonus, drawCardsAmount);
    }

    @Override
    // Add 'Player target' to the inputs
    public void applyDrawEffect() {
        // TODO: Logic to apply effect to player target target.setStat(stat, value);
    }

    // Shouldn't really be used
    @Override
    public void destroyCard() {
        // TODO: destroy card logic to remove from the card from the deck
    }

    public void instantEffect() {
        // TODO: logic to set player stat permanently
    }
}
