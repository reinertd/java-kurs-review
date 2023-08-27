package de.viadee.javakurs.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.viadee.javakurs.model.GameState;
import de.viadee.javakurs.model.Player;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GameService {

    private GameState gameState;

    private final BehaviorSubject<GameState> gameState$ = BehaviorSubject.createDefault(getInitialGameState());

    private final ArrayList<Integer> pressedKeys = new ArrayList<>();

    protected BufferedImage levelStreetmap;

    private FirestoreService firestoreService;

    public GameService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
        if(firestoreService != null) {
            if(!firestoreService.logIntoFirebase()) {
                this.firestoreService = null;
            }
        }
        if (this.firestoreService != null) {
            firestoreService.updateGamestate(this.gameStateToJSON(this.gameState$.getValue()));
            firestoreService.getGamestates().doOnNext(
                    (s) -> {
                        this.gameState$.onNext(this.jsonToGameState(s));
                        this.gameLoop(0l);
                    }
            ).subscribe();
        }
        this.loadStreets();
        if (this.firestoreService == null) {
            Observable.interval(34, TimeUnit.MILLISECONDS)
                    .doOnNext(this::gameLoop)
                    .subscribe();
        }
    }

    public void loadStreets() {
        try {
            this.levelStreetmap = ImageIO.read(getClass().getResource("/level1_streets.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private synchronized void gameLoop(Long aLong) {
        this.gameState = this.gameState$.getValue();
        if (this.gameState != null && this.gameState.playerPosition != null && !this.gameState.won) {
            // Move to the right
            if (this.pressedKeys.contains(KeyEvent.VK_RIGHT) && canMove(this.gameState.playerPosition, 5, 0)) {
                this.gameState.playerPosition.translate(5, 0);
            }
            // Move to the left
            if (this.pressedKeys.contains(KeyEvent.VK_LEFT) && canMove(this.gameState.playerPosition, -5, 0)) {
                this.gameState.playerPosition.translate(-5, 0);
            }
            // Move up
            if (this.pressedKeys.contains(KeyEvent.VK_UP) && canMove(this.gameState.playerPosition, 0, -5)) {
                this.gameState.playerPosition.translate(0, -5);
            }
            // Move down
            if (this.pressedKeys.contains(KeyEvent.VK_DOWN) && canMove(this.gameState.playerPosition, 0, 5)) {
                this.gameState.playerPosition.translate(0, 5);
            }
            // Pizza delivered?
            if (!this.gameState.won && delivered(this.gameState.playerPosition)) {
                this.gameState.won = true;
            }
            if (firestoreService != null) {
                this.firestoreService.updateGamestate(this.gameStateToJSON(this.gameState));
            } else {
                this.gameState$.onNext(this.gameState);
            }
        }
    }

    private boolean canMove(Point player, int dx, int dy) {
        if (player != null) {
            final Point newPoint = new Point(player);
            newPoint.translate(dx, dy);
            // Nur rot ist relevant
            return ((levelStreetmap.getRGB(newPoint.x, newPoint.y) & 0xff0000) >> 16) > 128;
        }
        return false;
    }

    private boolean delivered(Point player) {
        if (player != null) {
            // Karte ist rot: grün ist kleiner 128
            return ((levelStreetmap.getRGB(player.x, player.y) & 0xff00) >> 8) < 128;
        }
        return false;
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
        if (firestoreService != null) {
            this.firestoreService.updateGamestate(this.gameStateToJSON(this.gameState));
        } else {
            this.gameState$.onNext(this.gameState);
        }
    }

    public synchronized void keyPressed(int keyCode) {
        if (!this.pressedKeys.contains(keyCode)) {
            this.pressedKeys.add(keyCode);
            if (firestoreService != null) {
                this.gameLoop(0l);
            }
        }
    }

    public synchronized void keyReleased(int keyCode) {
        if (this.pressedKeys.contains(keyCode)) {
            this.pressedKeys.remove((Integer) keyCode);
            if (firestoreService != null) {
                this.gameLoop(0l);
            }
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String gameStateToJSON(GameState gameState) {
        try {
            return this.objectMapper.writeValueAsString(gameState);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public GameState jsonToGameState(String gameState) {
        try {
            return this.objectMapper.readValue(gameState, GameState.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
