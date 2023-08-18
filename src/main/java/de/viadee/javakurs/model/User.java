package de.viadee.javakurs.model;

public abstract class User {

    protected String username;

    private final Boolean loggedIn;

    public User(String username, Boolean loggedIn) {
        this.username = username;
        this.loggedIn = loggedIn;
    }

    abstract Boolean isAdmin();

    public Boolean isLoggedIn() {
        return loggedIn;
    }

}
