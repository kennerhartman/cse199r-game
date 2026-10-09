package org.example.card;

import org.example.Character.PlayerCharacter;

// Action cards should be resolved immediately when played
public class ActionCard extends Card {

    int price;

    public ActionCard(String name, String description, CardStat stat, int bonus, int drawCardsAmount) {
        super(name, description, stat, bonus, drawCardsAmount);
    }

    @Override
    public void applyDrawEffect(PlayerCharacter character) {
        // TODO: Roll Bonus, player targeting and stat setting logic, remove printlns
        System.out.println("Applied Draw Effect");
    }

    @Override
    // Reverses bonus
    public void applyDiscardEffect(PlayerCharacter character){
        character.statBonus(this.stat.toString(), 0-this.bonus);
        System.out.println("Applied Discard Effect");
    }

    @Override
    // Only use when sold
    public void destroyCard() {
        // TODO: destroy card logic to remove from the card from the deck
        System.out.println("Card Destroyed");
    }

    @Override
    public CardType getType(){
        return CardType.ACTION;
    }

    // Called when the card is played
    public void rollBonus(int dice_sides, int number_of_dice) {
        // TODO: Call dice rolling function
        // this.bonus = rollDice(6, 2);
        this.bonus = 1;
        System.out.println("Bonus Rolled");
    }
}
