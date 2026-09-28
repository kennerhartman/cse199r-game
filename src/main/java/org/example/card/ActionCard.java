package org.example.card;

// Action cards should be resolved immediately when played
public class ActionCard extends Card {

    String stat;
    int bonus;
    int price;

    public ActionCard(String name, CardType type, String description, CardStat stat, int bonus, int drawCardsAmount) {
        super(name, type, description, stat, bonus, drawCardsAmount);
    }

    @Override
    public void applyDrawEffect() {
        // TODO: Roll Bonus, player targeting and stat setting logic
    }

    // Only use when sold
    public void destroyCard() {
        // TODO: destroy card logic to remove from the card from the deck
    }

    // Called when the card is played
    public void rollBonus(int dice_sides, int number_of_dice) {
        // TODO: Call dice rolling function
        // this.bonus = rollDice(6, 2);
    }
}
