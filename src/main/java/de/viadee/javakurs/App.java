package de.viadee.javakurs;

import de.viadee.javakurs.services.FirestoreService;
import de.viadee.javakurs.services.GameService;
import de.viadee.javakurs.services.UserService;
import de.viadee.javakurs.view.GameWindow;
import de.viadee.javakurs.view.LoginWindow;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class App implements WindowListener {

    public static String TITEL = "Dijavalo 5";

    private final JFrame mainWindow;

    final GameService gameService = new GameService(new FirestoreService());

    public App() {
        mainWindow = new JFrame(TITEL);
        mainWindow.addWindowListener(this);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setIconImage(new ImageIcon(getClass().getResource("/pizza.png")).getImage());

        // Initialize Sub-Windows
        this.gameWindow = new GameWindow(gameService);
        this.loginWindow = new LoginWindow(new UserService(gameService));

        // Switch to game after login
        gameService.getGameStateForPlayer()
                .filter(state-> state.player != null &&
                        state.player.isLoggedIn())
                .filter(state -> mainWindow.getContentPane() != gameWindow)
                .map(state -> state.player)
                .doOnNext((player) -> {
                    System.out.println("Switch to Game-Window");
                    this.switchToGameWindow();
                })
                .subscribe();

        // Switch to login
        SwingUtilities.invokeLater(this::switchToLoginWindow);
    }

    private final LoginWindow loginWindow;
    private final GameWindow gameWindow;

    public static void main(String[] args) {
        new App();
    }

    public void switchToLoginWindow() {
        mainWindow.setContentPane(loginWindow);
        mainWindow.pack();
        mainWindow.setSize(330, 200);
        mainWindow.setResizable(false);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }

    public void switchToGameWindow() {
        mainWindow.setContentPane(gameWindow);
        mainWindow.pack();
        mainWindow.setSize(GameWindow.WIDTH, GameWindow.HEIGHT);
        mainWindow.setResizable(false);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
        gameWindow.requestFocusInWindow();
    }

    @Override
    public void windowOpened(WindowEvent e) {    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.gameService.logOut();
    }

    @Override
    public void windowClosed(WindowEvent e) {    }

    @Override
    public void windowIconified(WindowEvent e) {    }

    @Override
    public void windowDeiconified(WindowEvent e) {    }

    @Override
    public void windowActivated(WindowEvent e) {    }

    @Override
    public void windowDeactivated(WindowEvent e) {    }
}