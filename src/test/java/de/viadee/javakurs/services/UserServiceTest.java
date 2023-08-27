package de.viadee.javakurs.services;

import org.junit.Assert;
import org.junit.Test;

public class UserServiceTest {

    GameService gameService = new GameService();

    UserService userService = new UserService(gameService);

    @Test
    public void emailIsOkTest() {
        Assert.assertTrue("E-Mail-Format ist ungueltig", userService.emailIsOk("test@test.de"));
    }

    @Test
    public void testPasswordLengthIsOkTest() {
        Assert.assertFalse("Passwort ist Ok, obwohl zu kurz", userService.passwordLengthIsOk(new char[]{' ',' ',' ',' '}));
        Assert.assertTrue("Passwort ist nicht Ok, obwohl lang genug", userService.passwordLengthIsOk(new char[]{' ',' ',' ',' ',' ',' ',' ',' ',' '}));
    }

    @Test
    public void testPasswordHasDigits() {
        Assert.assertFalse("Passwort ist Ok, obwohl es keine Zahlen enthaelt", userService.passwordHasDigits(new char[]{' '}));
        Assert.assertTrue("Passwort ist nicht Ok, obwohl es Zahlen enthaelt", userService.passwordHasDigits(new char[]{' ',' ','1',' '}));
    }

    @Test
    public void testPasswordHasUppercaseCharacters() {
        Assert.assertTrue("Passwort enthaelt keine Grossbuchstaben", userService.passwordHasUppercaseCharacters(new char[]{'a','A','b','c'}));
        Assert.assertFalse("Passwort enthaelt Grossbuchstaben ", userService.passwordHasUppercaseCharacters(new char[]{'a','b','c','d'}));
    }


    @Test
    public void loginTest() {
        Assert.assertEquals("Passwort ist nicht richtig, obwohl es korrekt sein sollte", UserService.LOGIN_OK, userService.login("test@test.de",new char[]{'T', 'e', 's', 't', '1', '2', '3', '$'}));
        Assert.assertEquals("Spieler ist Admin, obwohl er sich nicht als Admin angemeldet hat","Player",gameService.getPlayer().getClass().getSimpleName());
        Assert.assertEquals("Login ist nicht fehlgeschlagen, obwohl er fehlschlagen sollte", "Invalid e-mail ", userService.login("Hallo",new char[]{'1', '2', 'A', 'a', '$', 'w', 'R', 'd'}));
        Assert.assertEquals("Login ist nicht fehlgeschlagen, obwohl er fehlschlagen sollte", "Password is too short ", userService.login("test@test.de",new char[]{'1', '2', 'A', 'a', '$', 'w', 'R'}));
        Assert.assertEquals("Login ist nicht fehlgeschlagen, obwohl er fehlschlagen sollte", "Password has no digits ", userService.login("test@test.de",new char[]{'a', 'a', 'A', 'a', '$', 'w', 'R', 'd'}));
        Assert.assertEquals("Login ist nicht fehlgeschlagen, obwohl er fehlschlagen sollte", "Password has no upper case characters ", userService.login("test@test.de",new char[]{'1', '2', 'a', 'a', '$', 'w', 'r', 'd'}));
        Assert.assertEquals("Login ist nicht fehlgeschlagen, obwohl er fehlschlagen sollte", "Password has no lower case characters ", userService.login("test@test.de",new char[]{'1', '2', 'A', 'A', '$', 'W', 'R', 'D'}));
        Assert.assertEquals("Passwort ist nicht richtig, obwohl es korrekt sein sollte", "User or password incorrect ", userService.login("t@test.de",new char[]{'T', 'e', 's', 't', '1', '2', '3', '$'}));
    }
}