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

    private GameState[] gameState;

    private final BehaviorSubject<GameState[]> gameState$ = BehaviorSubject.createDefault(getInitialGameState());

    private final ArrayList<Integer> pressedKeys = new ArrayList<>();

    protected BufferedImage levelStreetmap;

    protected int playerNumber;

    private FirestoreService firestoreService;

    public GameService(FirestoreService firestoreService) {
        this.playerNumber = 0;
        this.firestoreService = firestoreService;
        if(firestoreService != null) {
            if(!firestoreService.logIntoFirebase()) {
                this.firestoreService = null;
            }
        }
        if (this.firestoreService != null) {
            firestoreService.getGamestates(1).doOnNext(
                    (s) -> {
                        GameState[] gameStates = this.gameState$.getValue();
                        gameStates[0] = this.jsonToGameState(s);
                        this.gameState$.onNext(gameStates);
                        this.gameLoop(0l);
                    }
            ).subscribe();
            firestoreService.getGamestates(2).doOnNext(
                    (s) -> {
                        GameState[] gameStates = this.gameState$.getValue();
                        gameStates[1] = this.jsonToGameState(s);
                        this.gameState$.onNext(gameStates);
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
        if(this.playerNumber > 0) {
            if (this.gameState != null && this.gameState[playerNumber-1].playerPosition != null && !this.gameState[playerNumber-1].won) {
                // Move to the right
                if (this.pressedKeys.contains(KeyEvent.VK_RIGHT) && canMove(this.gameState[playerNumber-1].playerPosition, 5, 0)) {
                    this.gameState[playerNumber-1].playerPosition.translate(5, 0);
                }
                // Move to the left
                if (this.pressedKeys.contains(KeyEvent.VK_LEFT) && canMove(this.gameState[playerNumber-1].playerPosition, -5, 0)) {
                    this.gameState[playerNumber-1].playerPosition.translate(-5, 0);
                }
                // Move up
                if (this.pressedKeys.contains(KeyEvent.VK_UP) && canMove(this.gameState[playerNumber-1].playerPosition, 0, -5)) {
                    this.gameState[playerNumber-1].playerPosition.translate(0, -5);
                }
                // Move down
                if (this.pressedKeys.contains(KeyEvent.VK_DOWN) && canMove(this.gameState[playerNumber-1].playerPosition, 0, 5)) {
                    this.gameState[playerNumber-1].playerPosition.translate(0, 5);
                }
                // Pizza delivered?
                if (!this.gameState[playerNumber-1].won && delivered(this.gameState[playerNumber-1].playerPosition)) {
                    this.gameState[playerNumber-1].won = true;
                }
                if (firestoreService != null) {
                    this.firestoreService.updateGamestate(this.playerNumber, this.gameStateToJSON(this.gameState[playerNumber-1]));
                } else {
                    this.gameState$.onNext(this.gameState);
                }
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

    private GameState[] getInitialGameState() {
        final GameState newGame1 = new GameState();
        newGame1.won = false;
        final GameState newGame2 = new GameState();
        newGame2.won = false;
        return new GameState[]{newGame1, newGame2};
    }

    public Observable<GameState[]> getGameState() {
        return gameState$;
    }

    public Observable<GameState> getGameStateForPlayer() {
        return gameState$
                .filter(g->this.playerNumber>0)
                .map(g -> g[this.playerNumber-1]);
    }

    public synchronized void setPlayer(Player player) {
        this.gameState = this.gameState$.getValue();
        for(int i = 0; i< this.gameState.length; i++) {
            if(this.playerNumber == 0 &&
                    (this.gameState[i].player == null
                            || this.gameState[i].player.getUsername()==null
                            || this.gameState[i].player.getUsername().equals(player.getUsername()))) {
                this.playerNumber = i+1;
                this.gameState[i].player = player;
            }
        }
        if(this.playerNumber == 0 &&
                (this.gameState[1].player == null
                        || this.gameState[1].player.getUsername()==null
                        || player.getUsername().equals("test@test.de"))) {
            this.playerNumber = 1;
            this.gameState[0].player = player;
        } else if(this.playerNumber == 0) {
            this.playerNumber = 2;
            this.gameState[1].player = player;
        }
        this.gameState[0].playerPosition = new Point(150, 250);
        this.gameState[1].playerPosition = new Point(145, 135);
        this.gameState[0].won = false;
        this.gameState[1].won = false;
        if (firestoreService != null) {
            this.gameState[this.playerNumber-1].player.setLoggedIn(true);
            this.firestoreService.updateGamestate(this.playerNumber,this.gameStateToJSON(this.gameState[playerNumber-1]));
        } else {
            this.gameState$.onNext(this.gameState);
        }
    }

    public synchronized void logOut() {
        if(this.gameState[this.playerNumber-1].player!=null) {
            this.gameState[this.playerNumber-1].player.setLoggedIn(false);
        }
        this.gameState[0].won = false;
        this.gameState[1].won = false;
        if (firestoreService != null) {
            this.firestoreService.updateGamestate(this.playerNumber,this.gameStateToJSON(this.gameState[playerNumber-1]));
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
