package org.example.card;

/**
 Ability cards are gained during character creation and last the duration of the character playthrough.
 <p>
 They have an instant effect during character creation which sets the base stat to a number, and a draw effect
 which is used during combat.
 */
public class AbilityCard extends Card {
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

    public void instantEffect() {
        // TODO: logic to set player stat permanently
    }
}
