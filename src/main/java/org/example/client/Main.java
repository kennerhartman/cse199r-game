package org.example.client;

import static com.raylib.Raylib.FLAG_WINDOW_RESIZABLE;
import static com.raylib.Raylib.KEY_NULL;

import com.raylib.Raylib;
//Pseudo game imports
import org.example.Character.GameSaveData;
import org.example.Character.PlayerCharacter;
import org.example.GameLoop.Combat;
import org.example.GameLoop.CombatAction;
import org.example.GameLoop.LoadSystem;
import org.example.GameLoop.SaveSystem;
import org.example.Player.Player;
import org.example.card.AbilityCard;
import org.example.card.ActionCard;
import org.example.card.Card;
import org.example.card.CardStorage;
import org.example.core.registries.Registries;
import org.example.enemies.Enemy;
import org.example.map.MapManager;
import org.example.map.MapNode;

import java.util.Scanner;

public class Main {


    static void main(String[] args) {
        Raylib.InitWindow(850, 500, "Hero of the Draw");
        Raylib.SetWindowState(FLAG_WINDOW_RESIZABLE);
        Raylib.SetTargetFPS(60);
        Raylib.SetExitKey(KEY_NULL);

        Registries.load();

        Client client = new Client();

        while (!Raylib.WindowShouldClose()) {
            client.run();

            if (!client.isRunning()) {
                break;
            }
        }
    }
}
