package de.viadee.javakurs;

import de.viadee.javakurs.services.GameService;
import de.viadee.javakurs.services.UserService;
import de.viadee.javakurs.view.LoginWindow;

import javax.swing.*;

public class App {

    public static String TITEL = "Diavolo 5";

    private final JFrame mainWindow;

    public App() {
        mainWindow = new JFrame(TITEL);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setIconImage(new ImageIcon(getClass().getResource("/pizza.png")).getImage());

        // Initialize Sub-Windows
        final GameService gameService = new GameService();
        this.loginWindow = new LoginWindow(new UserService(gameService));

        // Switch to login
        SwingUtilities.invokeLater(this::switchToLoginWindow);
    }

    private final LoginWindow loginWindow;

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
}