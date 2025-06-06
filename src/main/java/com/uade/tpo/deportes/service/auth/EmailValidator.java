package com.uade.tpo.deportes.service.auth;

public class EmailValidator {
    public static boolean esEmailValido(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
}