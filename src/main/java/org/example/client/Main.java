package org.example.client;

import static com.raylib.Raylib.FLAG_WINDOW_RESIZABLE;

import com.raylib.Raylib;
//Pseudo game imports
import org.example.Character.PlayerCharacter;
import org.example.Player.Player;
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
//        Create new player and character
        Player newPlayer1 = new Player("Player 1", "Password123");
        PlayerCharacter newCharacter = new PlayerCharacter(newPlayer1, "Example");
//        Text based game input
        int choiceInput;
        Scanner scanner = new Scanner(System.in);
//        Start loop
        System.out.println("Pseudo Game Started\n Loading Deck...");
//        Create card storage from file
        CardStorage pseudoStorage = new CardStorage();
        pseudoStorage.loadDeck("./src/main/java/org/example/card/cardData.json");
//        Display cards
        System.out.println("PlayerCharacter Creation - Pick an Ability: ");
        pseudoStorage.loadStore(4, Card.CardType.ABILITY);
//        Ask for choice, register changes
        choiceInput = Integer.parseInt(scanner.nextLine());
        Card chosenCard = pseudoStorage.AllCards.get(choiceInput);
        if(chosenCard instanceof AbilityCard){
            AbilityCard ability = (AbilityCard) chosenCard;
            ability.instantEffect(newCharacter);
        }else{
            System.out.println("Not an ability Card");
        }
    }
}
