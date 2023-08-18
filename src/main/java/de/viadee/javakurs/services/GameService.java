package de.viadee.javakurs.services;

import de.viadee.javakurs.model.GameState;
import de.viadee.javakurs.model.User;
import java.util.ArrayList;

public class GameService {

    private GameState gameState;

    private final ArrayList<Integer> pressedKeys = new ArrayList<>();

    public GameService() {
        this.gameState = new GameState();
    }

    public synchronized void setPlayer(User player) {
        this.gameState.player = player;
    }

}
