package de.viadee.javakurs.view;

import de.viadee.javakurs.model.GameState;
import de.viadee.javakurs.services.GameService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GameWindow extends JPanel implements KeyListener {

    public static final int WIDTH = 640;

    public static final int HEIGHT = 400;

    private GameState state;

    private GameService gameService;

    protected BufferedImage levelBackground;

    protected BufferedImage player;

    public GameWindow(GameService gameService) {
        this.gameService = gameService;
        setOpaque(true);
        setBackground(Color.WHITE);
        setSize(WIDTH, HEIGHT);
        addKeyListener(this);
        setFocusable(true);
        loadBackground();
        loadPlayer();
        this.gameService.getGameState()
                .doOnNext((state) -> {
                    this.state = state;
                    this.repaint();
                })
                .subscribe();
    }

    public void loadBackground() {
        try {
            this.levelBackground = ImageIO.read(getClass().getResource("/level1.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPlayer() {
        try {
            this.player = ImageIO.read(getClass().getResource("/rad.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(this.levelBackground, 0, 0, this);
        if (this.state.playerPosition != null) {
            g.drawImage(this.player, this.state.playerPosition.x - 28, this.state.playerPosition.y - 28, this);
        }
        if (this.state.won) {
            Font font = new Font("Sans", Font.BOLD, 40);
            g.setFont(font);
            g.setColor(Color.YELLOW);
            g.drawString("Herzlichen Gl\u00fcckwunsch!", 80, 170);
            g.drawString("Pizza ausgeliefert.", 80, 220);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.gameService.keyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.gameService.keyReleased(e.getKeyCode());
    }
}
