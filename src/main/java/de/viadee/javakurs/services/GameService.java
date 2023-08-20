package de.viadee.javakurs.services;

import de.viadee.javakurs.model.GameState;
import de.viadee.javakurs.model.Player;
import de.viadee.javakurs.model.User;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.awt.*;
import java.util.ArrayList;

public class GameService {

    private GameState gameState;

    private final BehaviorSubject<GameState> gameState$ = BehaviorSubject.createDefault(getInitialGameState());

    private final ArrayList<Integer> pressedKeys = new ArrayList<>();

    public GameService() {
        this.gameState = new GameState();
    }

    private GameState getInitialGameState() {
        final GameState newGame = new GameState();
        newGame.won = false;
        return newGame;
    }

    public Observable<GameState> getGameState() {
        return gameState$;
    }

    public synchronized void setPlayer(Player player) {
        this.gameState = this.gameState$.getValue();
        this.gameState.player = player;
        this.gameState.playerPosition = new Point(150, 250);
        this.gameState$.onNext(this.gameState);
    }

    public synchronized User getPlayer() {
        return this.gameState.player;
    }

    public synchronized void keyPressed(int keyCode) {
        if (!this.pressedKeys.contains(keyCode)) {
            this.pressedKeys.add(keyCode);
        }
    }

    public synchronized void keyReleased(int keyCode) {
        if (this.pressedKeys.contains(keyCode)) {
            this.pressedKeys.remove((Integer) keyCode);
        }
    }

}
