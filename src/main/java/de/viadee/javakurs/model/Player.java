package de.viadee.javakurs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player extends User {
    public Player(@JsonProperty("username") String username, @JsonProperty("loggedIn") Boolean loggedIn) {
        super(username, loggedIn);
    }

    @Override
    Boolean isAdmin() {
        return false;
    }
}
