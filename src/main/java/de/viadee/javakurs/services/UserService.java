package de.viadee.javakurs.services;

import com.google.common.hash.Hashing;
import de.viadee.javakurs.model.PasswordValidator;
import org.apache.commons.validator.EmailValidator;

import java.nio.charset.StandardCharsets;

public class UserService {

    protected static final String LOGIN_OK = "Login Ok";

    private final EmailValidator emailValidator;

    private final PasswordValidator[] passwordValidators;

    public UserService() {
        this.emailValidator = EmailValidator.getInstance();
        this.passwordValidators = new PasswordValidator[1];
    }

    public boolean login(String email, char[] password) {
        final StringBuffer messages = new StringBuffer();
        // Validate E-Mail
        if(!this.emailIsOk(email)) {
            return false;
        }
        // Valiate Passwords
        if(!this.passwordLengthIsOk(password)) {
            return false;
        }
        if(!this.passwordHasDigits(password)) {
            return false;
        }
        // Check password
        return true;
    }

    protected boolean emailIsOk(String email) {
        if (!emailValidator.isValid(email)) {
            return false;
        } else {
            return true;
        }
    }

    protected boolean passwordLengthIsOk(char[] s) {
        if (s.length < 8) {
            return false;
        } else {
            return true;
        }
    }

    protected boolean passwordHasDigits(char[] password) {
        for(int i=0;i<password.length;i++){
            if((Character.isDigit(password[i]))) {
                return true;
            }
        }
        return false;
    }

    protected boolean passwordHasUppercaseCharacters(char[] password) {
        return false;
    }

    private boolean passwordIsCorrect(String username, char[] password) {
        final String hash = Hashing.sha256().
                hashString(String.valueOf(password), StandardCharsets.UTF_8).toString();
        if (username.equals("test@test.de") &&
                hash.equals("3faebbbfcae364da3f14ab80a2a826f3c27a7b6a8c763d8fe96b25d2f8334e94")) {
            // Login Ok, keine Fehlermeldung
            return true;
        }
        return false;
    }

}
