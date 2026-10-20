package org.example.client;

import static com.raylib.Raylib.FLAG_WINDOW_RESIZABLE;

import com.raylib.Raylib;
//Pseudo game imports
import org.example.Character.GameSaveData;
import org.example.Character.PlayerCharacter;
import org.example.GameLoop.LoadSystem;
import org.example.GameLoop.SaveSystem;
import org.example.Player.Player;
import org.example.card.AbilityCard;
import org.example.card.Card;
import org.example.card.CardStorage;
import org.example.core.registries.Registries;

import java.util.Scanner;

public class Main {


    static void main(String[] args) {
        Raylib.InitWindow(850, 500, "Hero of the Draw");
        Raylib.SetWindowState(FLAG_WINDOW_RESIZABLE);
        Raylib.SetTargetFPS(60);

        Registries.load();
        Client client = new Client();

        while (!Raylib.WindowShouldClose()) {
            client.run();
        }

//        Add Text based Game loop Here!
//        Create new player and character
        Player newPlayer1 = new Player("Player_1", "Password123");
        //        Text based game input
        int choiceInput;
        String nameIn;
        Scanner scanner = new Scanner(System.in);
//        Start Game, create character
        System.out.println("Pseudo Game Started\n Create Character...");
        System.out.print("Name: ");

        nameIn = scanner.nextLine();
        PlayerCharacter newCharacter = new PlayerCharacter(newPlayer1, nameIn);

//        Start loop
        System.out.println("Pseudo Game Started\n Loading Deck...");
//        Create card storage from file
        CardStorage pseudoStorage = new CardStorage();
        pseudoStorage.loadDeck();
//        Display cards
        System.out.println("PlayerCharacter Creation - Pick an Ability: ");
        pseudoStorage.loadStore(4, Card.CardType.ABILITY);
//        Ask for choice, register changes
        choiceInput = Integer.parseInt(scanner.nextLine());
        Card chosenCard = pseudoStorage.StoreCards.get(choiceInput);
        if(chosenCard instanceof AbilityCard){
            AbilityCard ability = (AbilityCard) chosenCard;
            ability.instantEffect(newCharacter);
            newCharacter.deck.drawPile.add(chosenCard);
        }else{
            System.out.println("Not an ability Card");
        }
        GameSaveData newSave = new GameSaveData(newCharacter, newPlayer1);
        SaveSystem.saveGame(newSave, 1);
        System.out.println("Load Saved Character?\n0. yes\n1. no");
        choiceInput = Integer.parseInt(scanner.nextLine());
        if(choiceInput == 0){
            GameSaveData loadedSave = LoadSystem.loadGame(1);
            PlayerCharacter loadedCharacter = LoadSystem.loadCharacter(pseudoStorage, loadedSave, newPlayer1);
            System.out.println("Character successfully loaded into Main: "+ loadedCharacter.name);
        }
    }
}
