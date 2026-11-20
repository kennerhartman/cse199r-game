package org.example.card;

import org.example.Character.PlayerCharacter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    public ArrayList<Card> drawPile = new ArrayList<Card>();
    public ArrayList<Card> discard = new ArrayList<Card>();
    public ArrayList<Card> hand = new ArrayList<Card>();
//    public ArrayList<Card> allCards = new ArrayList<Card>();
//    TODO: implement character class
    public PlayerCharacter owner;

    public Deck(PlayerCharacter owner){
        this.owner = owner;
    }

    //    Handles pulling a card from the draw pile and putting it into your hand
    public boolean draw() {
        return drawCard() != null;
    }

    public void fillHand() {
        while (hand.size() < owner.getStat("STRENGTH")) {
            if (!draw()) {
                break;
            }
        }
    }

//    Handles playing a card from your hand
    public boolean play(Card selectedCard, PlayerCharacter target) {

        if (!removeFromHand(selectedCard)) {
            return false;
        }

        selectedCard.applyPlayEffect(target);
        discard(selectedCard);

        return true;
    }

    // Places a card into the discard pile,
// or destroys it if it is single-use.
    public void discard(Card card) {

        if (card.isDestroyedOnPlay()) {
            card.destroyCard();
        } else {
            discard.add(card);
        }
    }

    // Discards a selected card directly from the player's hand.
// Also removes any passive effects provided by that card.
    public boolean discardFromHand(Card card) {

        if (!removeFromHand(card)) {
            return false;
        }

        // Move the card into the discard pile.
        // Ordinary discards should not trigger destroyOnPlay.
        discard.add(card);

        return true;
    }

//    Only used in special cases when the draw pile only has to be shuffled
    public void shuffleDrawPile(){
        Collections.shuffle(drawPile);
    }

//    Call at the beginning of each fight, and when the draw pile runs out
    public void shuffleAll() {

        drawPile.addAll(discard);
        discard.clear();

        Collections.shuffle(drawPile);
    }

//    Used when a card is bought
    public void addCard(Card card){
        discard.add(card);
    }

    public boolean removeFromHand(Card selectedCard) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) == selectedCard) {
                hand.remove(i);
                selectedCard.applyDiscardEffect(owner);
                return true;
            }
        }

        return false;
    }

    public Card drawCard() {
        if (drawPile.isEmpty()) {
            shuffleAll();
        }

        if (drawPile.isEmpty()) {
            return null;
        }

        Card card = drawPile.removeFirst();
        hand.add(card);
        card.applyDrawEffect(owner);

        return card;
    }

    // Returns a drawn card to the top of the draw pile.
//
// IMPORTANT GAMEPLAY RULE:
// Undoing a Strength action does NOT reverse reshuffles.
// Any shuffle triggered during planning remains in effect.
//
// This is intentional and allows strategic deck manipulation.
    public boolean undoDraw(Card card) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) == card) {
                hand.remove(i);
                card.applyDiscardEffect(owner);
                drawPile.addFirst(card);
                return true;
            }
        }

        return false;
    }
}
