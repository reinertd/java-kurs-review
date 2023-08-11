package de.viadee.javakurs;

import org.apache.commons.validator.EmailValidator;

public class App {

    public static void main(String[] args) {
        String email = "Hallo Bochum!";
        boolean mailOk = EmailValidator.getInstance().isValid(email);

        if(mailOk) {
            System.out.println("E-Mail ist Ok!");
        } else {
            System.out.println("E-Mail ist nicht gut!");
        }
    }

}