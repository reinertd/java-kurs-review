package de.viadee.javakurs.services;

import org.apache.commons.validator.EmailValidator;

public class UserService {

    protected static final String LOGIN_OK = "Login Ok";

    private final EmailValidator emailValidator;

    public UserService() {
        this.emailValidator = EmailValidator.getInstance();
    }

    public String login(String email) {
        final StringBuffer messages = new StringBuffer();
        // Validate E-Mail
        messages.append(this.validateEMail(email));
        if (messages.length() == 0) {
            return LOGIN_OK;
        } else {
            return messages.toString();
        }
    }

    public String validateEMail(String email) {
        if (!emailValidator.isValid(email)) {
            return "Invalid e-mail ";
        } else {
            return "";
        }
    }

}
