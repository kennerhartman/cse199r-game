package org.example.card;

// Ability cards are gained during character creation
// and last the duration of the character playthrough

// They have an instant effect during character creation
// which sets the base stat to a number, and a draw effect
// which is used during combat

public class Ability_Card extends Card{
//    The base value is Ability card specific, and is used to set
//    the default stat value of a character on creation
    int base;

    public Ability_Card(String name, String description_text, String stat, int base, int bonus, int draw_cards_amount){
        this.name = name;
        this.type = "Ability";
        this.description_text = description_text;
        this.stat = stat;
        this.base = base;
        this.bonus = bonus;
        this.draw_cards_amount = draw_cards_amount;
    }

    @Override
//    Add 'Player target' to the inputs
    public void apply_draw_effect(){
//       TODO: Logic to apply effect to player target
//        target.setStat(stat, value);
    }

    public void instant_effect(){
//        TODO: logic to set player stat permanently
    }
}
