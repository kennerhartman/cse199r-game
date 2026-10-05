package org.example.client;

import static com.raylib.Raylib.FLAG_WINDOW_RESIZABLE;

import com.raylib.Raylib;
//Pseudo game imports
import org.example.card.AbilityCard;
import org.example.card.Card;
import org.example.card.CardStorage;
import java.util.Scanner;

public class Main {


    static void main(String[] args) {
        Raylib.InitWindow(850, 500, "Hero of the Draw");
        Raylib.SetWindowState(FLAG_WINDOW_RESIZABLE);
        Raylib.SetTargetFPS(60);

        Client client = new Client();

        while (!Raylib.WindowShouldClose()) {
            client.run();
        }
//        Add Text based Game loop Here!
//        Fake character stats
        int HEALTH;
        int STRENGTH;
        int ATTACK;
        int SPEED;
        int choiceInput;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Pseudo Game Started\n Loading Deck...");
        CardStorage pseudoStorage = new CardStorage();
        pseudoStorage.loadDeck("./src/main/java/org/example/card/cardData.json");
        System.out.println("PlayerCharacter Creation - Pick an Ability: ");
        pseudoStorage.loadStore(8);
        choiceInput = Integer.parseInt(scanner.nextLine());
        Card chosenCard = pseudoStorage.AllCards.get(choiceInput);
        if(chosenCard instanceof AbilityCard){
            AbilityCard ability = (AbilityCard) chosenCard;
            ability.instantEffect();
        }else{
            System.out.println("Not an ability Card");
        }
    }
}
