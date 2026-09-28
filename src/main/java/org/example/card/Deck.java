package org.example.card;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    public ArrayList<Card> drawPile;
    public ArrayList<Card> discard;
    public ArrayList<Card> hand;
//    TODO: implement character class
//    public Character owner;

//    Handles pulling a card from the draw pile and putting it into your hand
    public void draw(){
//        Check if there is a card to draw, if not, shuffle discard into draw pile
        if(drawPile.isEmpty()){
            shuffleAll();
        }
//        Draw a new card, add it to the hand
        Card newCard = drawPile.getFirst();
        drawPile.removeFirst();
        hand.add(newCard);
    }

//    Handles playing a card from your hand
    public void play(){
//        TODO: Change this selector to detect player input and select a card based on that instead
        Card currentCard = hand.getFirst();
        hand.removeFirst();
        currentCard.applyDrawEffect();
        discard(currentCard);
    }

//    Handles moving cards from current hand to discard
    public void discard(Card card){
//        If the card is one time use, destroy, otherwise add it to the discard
        if(card.destroyOnPlay){
            card.destroyCard();
        }else{
            discard.add(card);
        }
    }

//    Only used in special cases when the draw pile only has to be shuffled
    public void shuffleDrawPile(){
        Collections.shuffle(drawPile);
    }

//    Call at the beginning of each fight, and when the draw pile runs out
    public void shuffleAll(){
        Collections.shuffle(discard);
        drawPile.addAll(discard);
    }

//    Used when a card is bought
    public void addCard(Card card){
        discard.add(card);
    }
}
