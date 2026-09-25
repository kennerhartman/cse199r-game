package org.example.client;

import com.raylib.Raylib;

public class Main {


    static void main(String[] args) {
        Raylib.InitWindow(850, 500, "Hero of the Draw");
        Raylib.SetTargetFPS(60);

        new Client();

        while (!Raylib.WindowShouldClose()) {
            Client.getInstance().run();
        }
    }
}
