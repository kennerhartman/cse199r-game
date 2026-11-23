package org.example.card;

import org.example.Character.PlayerCharacter;

import org.example.GameLoop.Combat;
import org.example.core.serializer.Codec;
import org.example.core.serializer.RecordCodecBuilder;

// Action cards should be resolved immediately when played
public class ActionCard extends Card {
    public static final Codec<ActionCard> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Card::getName),
                    Codec.STRING.fieldOf("description").forGetter(Card::getDescription),
                    Codec.ofEnum(CardStat.class).fieldOf("stat").forGetter(Card::getStat),
                    Codec.INT.fieldOf("bonus").forGetter(Card::getBonus),
                    Codec.INT.fieldOf("price").forGetter(Card::getPrice),
                    Codec.INT.fieldOf("draw_cards_amount").forGetter(Card::getDrawCardsAmount)
            ).apply(ActionCard::new)
    );

    int price;

    public ActionCard(String name, String description, CardStat stat, int bonus, int drawCardsAmount) {
        super(name, description, stat, 0, bonus, drawCardsAmount);
    }

    public ActionCard(String name, String description, CardStat stat, int price, int bonus, int drawCardsAmount) {
        super(name, description, stat, price, bonus, drawCardsAmount);
    }

    @Override
    public void applyDrawEffect(PlayerCharacter character) {
        // ActionCards do not activate when drawn.
    }

    @Override
    public void applyDiscardEffect(PlayerCharacter character) {
        // No passive effect to remove.
    }

    @Override
    public void applyPlayEffect(PlayerCharacter character) {
        switch (stat) {
            case ATTACK -> {
                // TODO: Add bonus to the turn's attack total.
                System.out.println("Attack bonus: " + bonus);
            }

            case HEALTH -> {
                character.heal(bonus);
                System.out.println(
                        character.name + " healed for " + bonus
                );
            }

            case STRENGTH, SPEED -> {
                // These effects already happened during planning.
                // Do not apply them again.
            }
        }
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

    @Override
    public void applyPlanningEffect(
            PlayerCharacter character,
            Combat combat
    ) {
        switch (stat) {
            case STRENGTH -> {
                for (int i = 0; i < bonus; i++) {
                    if (!character.deck.draw()) {
                        break;
                    }
                }
            }

            case SPEED -> {
                combat.addTemporaryActions(character, bonus);
            }

            default -> {
                // ATTACK and HEALTH resolve at turn end.
            }
        }
    }

    // Called when the card is played
    public void rollBonus(int dice_sides, int number_of_dice) {
        // TODO: Call dice rolling function
        // this.bonus = rollDice(6, 2);
        this.bonus = 1;
        System.out.println("Bonus Rolled");
    }
}
