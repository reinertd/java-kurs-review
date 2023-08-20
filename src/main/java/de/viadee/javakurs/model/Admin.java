package de.viadee.javakurs.model;

public class Admin extends User {

    public Admin(String username, Boolean loggedIn) {
        super(username, loggedIn);
    }

    @Override
    public Boolean isAdmin() {
        return this.isLoggedIn() && this.username.equals("admin@test.de");
    }
}
