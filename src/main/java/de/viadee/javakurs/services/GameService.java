package de.viadee.javakurs.services;

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

    public GameService() {
        this.loadStreets();
        Observable.interval(34, TimeUnit.MILLISECONDS)
                .doOnNext(this::gameLoop)
                .subscribe();
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
            // bewege den Spieler nach rechts
            if (this.pressedKeys.contains(KeyEvent.VK_RIGHT)) {
                this.gameState.playerPosition.translate(1, 0);
            }
            // TODO: bewege den Spieler in die anderen Richtungen
            // TODO: Prüfe, ob der Spieler überhaupt da hin darf, wo er hin möchte :-)
            this.gameState$.onNext(this.gameState);
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
        this.gameState$.onNext(this.gameState);
    }

    public synchronized Player getPlayer() {
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
