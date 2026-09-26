package org.example.card;


// Loot cards should affect any cards played
// on this turn after the loot card is played
// then be destroyed on turn end

public class Loot_Card extends Card{
//    Stat can be:
//    Health (number of health points)
//    Strength (number of cards in hand)
//    Speed (number of cards played per turn)
//    Attack (base damage)

    int price;

    public Loot_Card(String name, String description_text, String stat, int bonus, int price, int draw_cards_amount){
        this.name = name;
        this.type = "Loot";
        this.description_text = description_text;
        this.stat = stat;
        this.bonus = bonus;
        this.price = price;
        this.draw_cards_amount = draw_cards_amount;
    }

    @Override
//    Add 'Player target' to the inputs
    public void apply_draw_effect(){
//        TODO: Logic to apply effect to player target
//        target.setStat(stat, value);
//        Then wait for perform action
//        After actions, revert stat change and destroy card
    }

    // To be used when sold or played
    public void destroy_self(){
//      TODO: add destroy self logic to remove from deck
    }

}
