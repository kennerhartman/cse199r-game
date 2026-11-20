package org.example.GameLoop;

import org.example.card.Card;

import java.util.List;

public record PlanningDraw(
        CombatAction source,
        List<Card> drawnCards
) {}