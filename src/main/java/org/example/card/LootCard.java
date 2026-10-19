package org.example.card;

import org.example.Character.PlayerCharacter;

import org.example.core.serializer.Codec;
import org.example.core.serializer.RecordCodecBuilder;

/**
 * Loot cards should affect any cards played on this turn after the loot card is played then be destroyed on turn end.
 */
public class LootCard extends Card {
    public static final Codec<LootCard> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Card::getName),
                    Codec.STRING.fieldOf("description").forGetter(Card::getDescription),
                    Codec.ofEnum(CardStat.class).fieldOf("stat").forGetter(Card::getStat),
                    Codec.INT.fieldOf("bonus").forGetter(Card::getBonus),
                    Codec.INT.fieldOf("draw_cards_amount").forGetter(Card::getDrawCardsAmount),
                    Codec.INT.fieldOf("price").forGetter(LootCard::getPrice)
            ).apply(LootCard::new)
    );

    // Stat can be:
    // Health (number of health points)
    // Strength (number of cards in hand)
    // Speed (number of cards played per turn)
    // Attack (base damage)

    int price;

    public LootCard(String name, String description, CardStat stat, int bonus, int drawCardsAmount, int price) {
        super(name, description, stat, bonus, drawCardsAmount);

        this.price = price;
        this.destroyOnPlay = true;
    }

    @Override
    // Add 'Player target' to the inputs
    public void applyDrawEffect(PlayerCharacter character) {
        // TODO: Logic to apply effect to player target

        // target.setStat(stat, value);
        // Then wait for perform action
        // After actions, revert stat change and destroy card
        System.out.println("Applied Draw Effect");
    }

    @Override
    // Reverses bonus
    public void applyDiscardEffect(PlayerCharacter character){
        character.statBonus(this.stat.toString(), 0-this.bonus);
        System.out.println("Applied Discard Effect");
    }

    @Override
    // To be used when sold or played
    public void destroyCard() {
        // TODO: add destroy self logic to remove from deck
        System.out.println("Card Destroyed");
    }

    @Override
    public CardType getType(){
        return CardType.LOOT;
    }

    public int getPrice() {
        return this.price;
    }
}
