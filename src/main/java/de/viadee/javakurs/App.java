package de.viadee.javakurs;

import de.viadee.javakurs.services.UserService;

public class App {

    public static String TITEL = "Diavolo 5";

    public App() {
        UserService userService = new UserService();

        String email = "Hallo Bochum!";
        boolean mailOk = false;
        int zaehler;
        char zeichen = ' ';

        mailOk = email.equals("test@test.de");

        zaehler = 3;

        mailOk = email.charAt(zaehler) == zeichen;

        if(mailOk) {
            System.out.println("E-Mail ist Ok!");
        } else {
            System.out.println("E-Mail ist nicht gut!");
        }
    }

    public static void main(String[] args) {
        new App();
    }

}