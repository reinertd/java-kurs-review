package de.viadee.javakurs.model;

import de.viadee.javakurs.services.UserService;

public class PasswortToShortValidator implements PasswordValidator {

    @Override
    public String validate(char[] password) {
        if (password.length < 8) {
            return "Password to short ";
        } else {
            return UserService.LOGIN_OK;
        }
    }
}
