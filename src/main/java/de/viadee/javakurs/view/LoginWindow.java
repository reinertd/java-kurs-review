package de.viadee.javakurs.view;

import de.viadee.javakurs.services.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginWindow extends JPanel {

    private final JLabel message;

    public LoginWindow(UserService userService) {
        this.message = new JLabel("");
        setLayout(new FlowLayout());

        final JTextField email = new JTextField(20);
        JLabel emailLabel = new JLabel("E-Mail:");
        emailLabel.setLabelFor(email);
        add(emailLabel);
        add(email);

        add(Box.createRigidArea(new Dimension(14, 30)));

        final JPasswordField password = new JPasswordField(20);
        JLabel passwordLabel = new JLabel("Passwort:");
        passwordLabel.setLabelFor(password);
        ActionListener loginaction = (e) -> {
            message.setText(userService.login(email.getText()));
        };
        password.addActionListener(loginaction);
        add(passwordLabel);
        add(password);

        final JButton loginButton = new JButton("Login");
        ActionListener buttonaction = (e) -> {
            message.setText("Hallo Welt!");
            loginButton.setBackground(Color.GREEN);
        };
        loginButton.addActionListener(buttonaction);
        add(loginButton);

        add(Box.createRigidArea(new Dimension(300, 30)));

        add(this.message);

    }
}
