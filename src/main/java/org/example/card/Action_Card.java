package org.example.card;

// Action cards should be resolved immediately when played

public class Action_Card extends Card{

    String stat;
    int bonus;
    int price;

    public Action_Card(String name, String description_text, String stat, int bonus, int price, int draw_cards_amount){
        this.name = name;
        this.type = "Action";
        this.description_text = description_text;
        this.stat = stat;
        this.bonus = 0;
        this.price = price;
        this.draw_cards_amount = draw_cards_amount;
    }

    @Override
    public void apply_draw_effect(){
//        TODO: Roll Bonus, player targeting and stat setting logic
    }

    // Only use when sold
    public void destroy_self(){
//        TODO: destroy self logic to remove from deck
    }

//    Called when the card is played
    public void roll_bonus(int dice_sides, int number_of_dice){
//        TODO: Call dice rolling function
//        This.bonus = roll_dice(6, 2);
    }
}
