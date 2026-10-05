package org.example.card;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class CardStorage implements TypeAdapterFactory{
    public ArrayList<Card> AllCards = new ArrayList<Card>();
    public ArrayList<Card> StoreCards = new ArrayList<Card>();

    // Tracks what subclass should be created based on the "type" value in the JSON file
    RuntimeTypeAdapterFactory<Card> cardAdapter = RuntimeTypeAdapterFactory
            .of(Card.class, "type")
            .registerSubtype(AbilityCard.class, "ABILITY")
            .registerSubtype(LootCard.class, "LOOT")
            .registerSubtype(ActionCard.class, "ACTION");

    // Creates a GsonBuilder with the type adapter factory to differentiate subclasses
    public final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(cardAdapter)
            .create();

    // handles the reading and creation of the deck
    public void loadDeck(String filename){
        // Creates new file reader for card file
        try(FileReader reader = new FileReader(filename)){
            // Stores the type needed for creating the array list
            Type cardListType = new TypeToken<ArrayList<Card>>(){}.getType();
            // Deserializes JSON directly into an array list
            this.AllCards = gson.fromJson(reader, cardListType);
        }
        // Catches file not found and IOException
        // TODO: replace these errors with an actual error logger, and probably fix any loop breaking here
        catch(FileNotFoundException e) {
            System.out.println("File missing: \n" + e);
        }
        catch (IOException e){
            System.out.println("Failed to read configuration file due to an I/O error: \n" + e);
        }
    }

    // Used for the shop or character creation
    public void draw(){
//      Draw a new card, add it to the hand
        Card newCard = AllCards.getFirst();
        AllCards.removeFirst();
        AllCards.add(newCard);
        StoreCards.add(newCard);
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

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        return null;
    }
}
