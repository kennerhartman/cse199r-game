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
                    Codec.INT.fieldOf("price").forGetter(Card::getPrice),
                    Codec.INT.fieldOf("bonus").forGetter(Card::getBonus),
                    Codec.INT.fieldOf("draw_cards_amount").forGetter(Card::getDrawCardsAmount)
            ).apply(LootCard::new)
    );

    // Stat can be:
    // Health (number of health points)
    // Strength (number of cards in hand)
    // Speed (number of cards played per turn)
    // Attack (base damage)

    public LootCard(String name, String description, CardStat stat, int price, int bonus, int drawCardsAmount) {
        super(name, description, stat, price, bonus, drawCardsAmount);

        this.destroyOnPlay = true;
    }

    @Override
    public void applyDrawEffect(PlayerCharacter character) {
        // No effect when drawn.
    }

    @Override
    public void applyDiscardEffect(PlayerCharacter character) {
        // No passive hand effect to remove.
    }

    @Override
    public void applyPlayEffect(PlayerCharacter character) {
        // TODO: Apply temporary turn bonus.
        System.out.println("Played LootCard: " + this.name);
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
