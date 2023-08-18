package de.viadee.javakurs.services;

import org.junit.Assert;
import org.junit.Test;

public class UserServiceTest {

    UserService userService = new UserService();

    @Test
    public void emailIsOkTest() {
        Assert.assertEquals(true, userService.emailIsOk("test@test.de"));
    }

    @Test
    public void loginTest() {
        Assert.assertEquals(true, userService.login("test@test.de",new char[]{' '}));
    }
}