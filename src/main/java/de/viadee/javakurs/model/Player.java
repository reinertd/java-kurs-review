package de.viadee.javakurs.model;

public class Player extends User {
    public Player(String username, Boolean loggedIn) {
        super(username, loggedIn);
    }

    @Override
    Boolean isAdmin() {
        return false;
    }
}
