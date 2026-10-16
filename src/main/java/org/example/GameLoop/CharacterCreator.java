package org.example.GameLoop;

import org.example.Character.GameSaveData;
import org.example.Character.PlayerCharacter;
import org.example.Player.Player;
import org.example.card.Card;
import org.example.card.CardStorage;

import java.util.Arrays;
import java.util.List;

public class CharacterCreator {
    public PlayerCharacter createCharacter(Player target, CardStorage storage){
        String name = "";
//        TODO: Ask player for input and set name to input
//         (Add some character limit and content censoring for validation)
        PlayerCharacter newCharacter = new PlayerCharacter(target, name);
        GameSaveData saveData = new GameSaveData(newCharacter, target);
//        storage.loadStore(8, Card.CardType.ABILITY);
        List<Card.CardStat> stats = Arrays.asList(Card.CardStat.values());
        for(int i = 0; i < 4; i++){
            storage.characterCreatorStore(Card.CardType.ABILITY, stats.get(i));
//            TODO: Visuals connected to the logic, and selecting based on mouse click
//          AbilityCard chosenCard = (AbilityCard) storage.StoreCards.get(choiceInput);
//          chosenCard.instantEffect(newCharacter);
//          newCharacter.deck.drawPile.add(chosenCard)
        }
//        TODO: Not sure if this is actually the best place to implement a save since there will also be game
//         data saved into the file and the character creator doesnt necessarily require access to that
//         information
//        SaveSystem.saveGame(saveData,1);
        return newCharacter;
    }
}
