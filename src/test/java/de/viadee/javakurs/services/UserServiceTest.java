package de.viadee.javakurs.services;

import org.junit.Assert;
import org.junit.Test;

public class UserServiceTest {

    UserService userService = new UserService();

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
        Assert.assertTrue("Passwort ist nicht richtig, obwohl es korrekt sein sollte", userService.login("test@test.de",new char[]{'T', 'e', 's', 't', '1', '2', '3', '$'}));
        Assert.assertFalse("Login ist nicht fehlgeschlagen, obwohl er fehlschlagen sollte", userService.login("Hallo",new char[]{'1', '2', 'A', 'a', '$', 'w', 'R', 'd'}));
        Assert.assertFalse("Login ist nicht fehlgeschlagen, obwohl er fehlschlagen sollte", userService.login("test@test.de",new char[]{'1', '2', 'A', 'a', '$', 'w', 'R'}));
        Assert.assertFalse("Login ist nicht fehlgeschlagen, obwohl er fehlschlagen sollte", userService.login("test@test.de",new char[]{'a', 'a', 'A', 'a', '$', 'w', 'R', 'd'}));
        Assert.assertFalse("Login ist nicht fehlgeschlagen, obwohl er fehlschlagen sollte", userService.login("test@test.de",new char[]{'1', '2', 'a', 'a', '$', 'w', 'r', 'd'}));
        // TODO: Bonus
        //  Assert.assertFalse("Login ist nicht fehlgeschlagen, obwohl er fehlschlagen sollte", userService.login("test@test.de",new char[]{'1', '2', 'A', 'A', '$', 'W', 'R', 'D'}));
        //  Assert.assertFalse("Passwort ist nicht richtig, obwohl es korrekt sein sollte", userService.login("t@test.de",new char[]{'T', 'e', 's', 't', '1', '2', '3', '$'}));
    }
}