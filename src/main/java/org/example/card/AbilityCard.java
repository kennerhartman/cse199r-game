package org.example.card;

import org.example.Character.PlayerCharacter;

import org.example.core.serializer.Codec;
import org.example.core.serializer.RecordCodecBuilder;

/**
 Ability cards are gained during character creation and last the duration of the character playthrough.
 <p>
 They have an instant effect during character creation which sets the base stat to a number, and a draw effect
 which is used during combat.
 */
public class AbilityCard extends Card {
    public static final Codec<AbilityCard> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Card::getName),
                    Codec.STRING.fieldOf("description").forGetter(Card::getDescription),
                    Codec.ofEnum(CardStat.class).fieldOf("stat").forGetter(Card::getStat),
                    Codec.INT.fieldOf("bonus").forGetter(Card::getBonus),
                    Codec.INT.fieldOf("draw_cards_amount").forGetter(Card::getDrawCardsAmount)
            ).apply(AbilityCard::new)
    );

    /**
     * The base value is Ability card specific, and is used to set the default stat value of a character on creation.
     */
    int base;

    public AbilityCard(String name, String description, CardStat stat, int bonus, int drawCardsAmount) {
        super(name, description, stat, bonus, drawCardsAmount);
    }

    @Override
    // Add 'Player target' to the inputs
    public void applyDrawEffect(PlayerCharacter character) {
        // TODO: TEST Logic to apply effect to player target target.setStat(stat, value);
        character.statBonus(this.stat.toString(), this.bonus);
        System.out.println("Applied Draw Effect");
    }

    @Override
    // Reverses bonus
    public void applyDiscardEffect(PlayerCharacter character){
        character.statBonus(this.stat.toString(), 0-this.bonus);
        System.out.println("Applied Discard Effect");
    }

    // Shouldn't really be used
    @Override
    public void destroyCard() {
        // TODO: destroy card logic to remove from the card from the deck
        System.out.println("Card Destroyed");
    }

    @Override
    public CardType getType(){
        return CardType.ABILITY;
    }

    // Sets a playercharacter's stat to the base value of the ability card
    public void instantEffect(PlayerCharacter character) {
        character.permaChangeStat(this.stat.toString(), this.base);
        System.out.println(character.name + "'s " + this.stat + " has been changed to "+ character.getStat(this.stat.toString()));
    }
}
