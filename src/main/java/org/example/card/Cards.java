package org.example.card;

import java.util.List;

import org.example.core.registries.Registries;

public class Cards {
    // TODO: DECLARE STATIC FIELDS FOR EACH CARD'S ID

    public static List<Card> getCardsByStat(Card.CardStat stat) {
        return Registries.CARDS
                .stream()
                .filter(card -> card.getStat() == stat)
                .toList();
    }
}
