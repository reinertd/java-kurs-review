package de.viadee.javakurs.model;

import de.viadee.javakurs.services.UserService;

public class PasswordTooShort implements PasswordValidator {

    @Override
    public String validate(char[] password) {
        if (password.length < 8) {
            return "Password is too short ";
        } else {
            return UserService.LOGIN_OK;
        }
    }
}
