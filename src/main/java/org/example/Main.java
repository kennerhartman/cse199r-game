package org.example;

import java.util.Map;

import org.example.card.Card;
import org.example.core.registries.Registries;
import org.example.util.Identifier;

public class Main {
    static void main(String[] args) {
        Registries.load();

        for (Map.Entry<Identifier, Card> card : Registries.CARDS) {
            System.out.println(card.getKey() + ": " + card.getValue().getType());
        }

        Card strike = Registries.CARDS.getValue(Identifier.ofDefault("strike"));
        System.out.println(strike.getName());
    }
}
