package de.viadee.javakurs.services;

import org.junit.Assert;
import org.junit.Test;

public class UserServiceTest {

    UserService userService = new UserService();

    @Test
    public void loginTest() {
        Assert.assertEquals(UserService.LOGIN_OK, userService.login("test@test.de"));
    }
}