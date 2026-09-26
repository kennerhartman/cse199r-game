package org.example.card;

abstract class Card {
//    All cards have a name or title
    String name;
//    All cards have a type, which can be:
//    Ability (permanent bonus from character creation)
//    Loot (one time use found in fights or occasionally the store)
//    Action (last the duration of the run, bought from store)
    String type;
//    All cards have a short instructional message that describes the function
    String description_text;
//    Stat can be:
//    Health (number of health points)
//    Strength (number of cards in hand)
//    Speed (number of cards played per turn)
//    Attack (base damage)
    String stat;
//    Bonus is the amount to add to a specific stat
//    when the card is played
    int bonus;
//    Can only be used if stat is "Strength",
//    allows an amount of cards to be drawn
    int draw_cards_amount;

    abstract void apply_draw_effect();
}
