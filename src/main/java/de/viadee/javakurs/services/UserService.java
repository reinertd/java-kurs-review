package de.viadee.javakurs.services;

import com.google.common.hash.Hashing;
import de.viadee.javakurs.model.Admin;
import de.viadee.javakurs.model.PasswordValidator;
import de.viadee.javakurs.model.PasswortToShortValidator;
import de.viadee.javakurs.model.Player;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.validator.EmailValidator;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UserService {

    public static final String LOGIN_OK = "Login Ok";

    private final EmailValidator emailValidator;

    private final PasswordValidator[] passwordValidators;

    private final GameService gameService;

    public UserService(GameService gameService) {
        this.gameService = gameService;
        this.emailValidator = EmailValidator.getInstance();
        this.passwordValidators = new PasswordValidator[4];
        this.passwordValidators[0] = new PasswortToShortValidator();
        this.passwordValidators[1] = new PasswordValidator() {
            @Override
            public String validate(char[] password) {
                for(int i=0;i<password.length;i++){
                    if((Character.isDigit(password[i]))) {
                        return UserService.LOGIN_OK;
                    }
                }
                return "Password has no digits ";
            }
        };
        this.passwordValidators[2] = password -> {
            for(int i=0;i<password.length;i++){
                if((Character.isUpperCase(password[i]))) {
                    return UserService.LOGIN_OK;
                }
            }
            return "Password has no upper case characters ";
        };
        this.passwordValidators[3] = password -> Arrays.asList(ArrayUtils.toObject(password))
                .stream().anyMatch(Character::isLowerCase)?UserService.LOGIN_OK:"Password has no lower case characters ";
    }

    public String login(String email, char[] password) {
        final StringBuffer messages = new StringBuffer();
        // Validate E-Mail
        if(!this.emailIsOk(email)) {
            return "Invalid e-mail ";
        }
        // Validate Passwords
        for (int i = 0; i < passwordValidators.length; i++) {
            final String passwordMessage = passwordValidators[i] != null?
                    passwordValidators[i].validate(password):UserService.LOGIN_OK;
            if (!LOGIN_OK.equals(passwordMessage)) {
                messages.append(passwordMessage);
            }
        }
        // Check password
        if(messages.length() == 0 && !this.passwordIsCorrect(email,password)) {
            return "User or password incorrect ";
        }
        if (this.gameService != null && messages.length() == 0) {
            if(email.equals("admin@test.de")) {
                gameService.setPlayer(new Admin(email, true));
            } else {
                gameService.setPlayer(new Player(email,true));
            }
            return LOGIN_OK;
        }
        return messages.toString();
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
        for(int i=0;i<password.length;i++){
            if((Character.isUpperCase(password[i]))) {
                return true;
            }
        }
        return false;
    }

    protected boolean passwordHasLowercaseCharacters(char[] password) {
        for(int i=0;i<password.length;i++){
            if((Character.isLowerCase(password[i]))) {
                return true;
            }
        }
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
        if (username.equals("admin@test.de") &&
                hash.equals("22b7dec7305d63e2c769b0c9141114e69a194cc853b444c73b7be3a0771b628a")) {
            // Login Ok, keine Fehlermeldung
            return true;
        }
        if (username.equals("test2@test.de") &&
                hash.equals("05e68a30269aa6d361fb5f657a321895f58388699d7539ac4ae43a7b50867f06")) {
            // Login Ok, keine Fehlermeldung
            return true;
        }
        return false;
    }

}
