package org.example.card;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.example.core.registries.Registries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardStorage implements TypeAdapterFactory{
    public ArrayList<Card> AllCards = new ArrayList<Card>();
    public ArrayList<Card> StoreCards = new ArrayList<Card>();

    // handles the reading and creation of the deck
    public void loadDeck(){
//        Uses Codec to populate AllCards
        List<Card> cards = Registries.CARDS.stream().toList();
        this.AllCards = new ArrayList<Card>(cards);
    }


    // Used for the shop or character creation
    private void draw(){
//      Draw a new card, add it to the store
        Card newCard = AllCards.getFirst();
        AllCards.removeFirst();
        AllCards.add(newCard);
        StoreCards.add(newCard);
    }

//    Overloaded draw that takes a type
    private void draw(Card.CardType type ){
    //      Draw a new card, add it to the store
        boolean cardloop = true;
        int numChecked = 0;
        while (cardloop & numChecked < AllCards.size()){
            Card newCard = AllCards.getFirst();
            AllCards.removeFirst();
            AllCards.add(newCard);
            if(newCard.getType() == type){
                StoreCards.add(newCard);
                break;
            }
//            TODO: Replace with actual error logging
            System.out.println(newCard.name + " type in list: " + newCard.getType() + " | Target type: " + type);
            numChecked ++;
        }
    }

    private void draw(Card.CardType type, Card.CardStat stat ){
        //      Draw a new card, add it to the store
        boolean cardloop = true;
        int numChecked = 0;
        while (cardloop & numChecked < AllCards.size()){
            Card newCard = AllCards.getFirst();
            AllCards.removeFirst();
            AllCards.add(newCard);
            if(newCard.getType() == type && newCard.getStat() == stat){
                StoreCards.add(newCard);
                break;
            }
//            TODO: Replace with actual error logging
            System.out.println(newCard.name + " type in list: " + newCard.getType() + " | Target type: " + type);
            numChecked ++;
        }
    }

    // Creates and displays the store
    public void loadStore(int numberOfCards){
        Collections.shuffle(AllCards);
        for(int i = 0; i<numberOfCards; i++){
            draw();
        }
        System.out.println("Store:");
        for(int i = 0; i<numberOfCards; i++) {
            System.out.println(i + ". " + this.StoreCards.get(i).name);
        }
    }

//    Overloaded loadStore that selects by type so that you can display a loot card after a battle or get
//    just ability cards for character creation
    public void loadStore(int numberOfCards, Card.CardType type){
        Collections.shuffle(AllCards);
        for(int i = 0; i<numberOfCards; i++){
            draw(type);
        }
        System.out.println("Store:");
        for(int i = 0; i<numberOfCards; i++) {
            System.out.println(i + ". " + this.StoreCards.get(i).name);
        }
    }

//      This method is mostly for character creation, in order to display two cards of a certain type and stat at once
    public void characterCreatorStore(Card.CardType type, Card.CardStat stat){
        Collections.shuffle(AllCards);
        for(int i = 0; i<2; i++){
            draw(type, stat);
        }
        System.out.println("Store:");
        for(int i = 0; i<2; i++) {
            System.out.println(i + ". " + this.StoreCards.get(i).name);
        }
    }

    public Card findCard(String cardName){
        int numChecked = 0;
        while(numChecked < AllCards.size()){
            Card newCard = AllCards.getFirst();
            if(newCard.name.equals(cardName)){
                return newCard;
            }
            AllCards.removeFirst();
            AllCards.add(newCard);
            numChecked++;
        }
        System.out.println("Error: No card "+ cardName + " found; Adding null card");
        return new AbilityCard("Card_Not_Found", "Card_Not_Found: This card is missing from your Card Storage!\nHelp: Check cardData.json;", Card.CardStat.HEALTH, 0, 0, 0);
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        return null;
    }
}
