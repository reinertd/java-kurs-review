package de.viadee.javakurs.view;

import de.viadee.javakurs.services.UserService;

import javax.swing.*;
import java.awt.*;

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

        final JButton loginButton = new JButton("Login");
        add(loginButton);

        add(Box.createRigidArea(new Dimension(300, 30)));

        add(this.message);

    }
}
