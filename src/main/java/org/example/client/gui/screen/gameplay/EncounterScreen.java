package org.example.client.gui.screen.gameplay;

import org.example.Character.PlayerCharacter;
import org.example.Player.Player;
import org.example.client.Client;
import org.example.client.gui.screen.Screen;
import org.example.map.Encounter;

public abstract class EncounterScreen extends Screen {
    protected final PlayerCharacter player;
    protected Encounter encounter;

    public EncounterScreen(Client client) {
        super(client);

        this.player = client.player.getCharacter();
        this.encounter = new Encounter();
    }
}
