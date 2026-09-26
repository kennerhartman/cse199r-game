package org.example.card;

/**
 * Loot cards should affect any cards played on this turn after the loot card is played then be destroyed on turn end.
 */
public class LootCard extends Card {
    // Stat can be:
    // Health (number of health points)
    // Strength (number of cards in hand)
    // Speed (number of cards played per turn)
    // Attack (base damage)

    int price;

    public LootCard(String name, CardType type, String description, CardStat stat, int bonus, int drawCardsAmount, int price) {
        super(name, type, description, stat, bonus, drawCardsAmount);

        this.price = price;
    }

    @Override
    // Add 'Player target' to the inputs
    public void applyDrawEffect() {
        // TODO: Logic to apply effect to player target

        // target.setStat(stat, value);
        // Then wait for perform action
        // After actions, revert stat change and destroy card
    }

    // To be used when sold or played
    public void destroyCard() {
        // TODO: add destroy self logic to remove from deck
    }
}
