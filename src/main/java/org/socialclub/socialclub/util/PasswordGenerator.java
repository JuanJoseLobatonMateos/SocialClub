// src/main/java/org/socialclub/socialclub/util/PasswordGenerator.java
package org.socialclub.socialclub.util;

import java.security.SecureRandom;

/**
 * Esta clase de utilidad proporciona métodos para generar contraseñas aleatorias.
 */
public class PasswordGenerator {

    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?";
    private static final String ALL_CHARACTERS = UPPER_CASE + LOWER_CASE + DIGITS + SPECIAL_CHARACTERS;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Constructor privado para evitar la instanciación de esta clase de utilidad.
     */
    private PasswordGenerator() {
        throw new UnsupportedOperationException("Clase de utilidad");
    }

    /**
     * Genera una contraseña aleatoria de longitud 8 caracteres.
     * La contraseña contendrá al menos una letra mayúscula, una letra minúscula, un dígito y un carácter especial.
     *
     * @return Una contraseña aleatoria con los requisitos especificados.
     */
    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder(8);

        password.append(UPPER_CASE.charAt(RANDOM.nextInt(UPPER_CASE.length())));
        password.append(LOWER_CASE.charAt(RANDOM.nextInt(LOWER_CASE.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));

        for (int i = 4; i < 8; i++) {
            password.append(ALL_CHARACTERS.charAt(RANDOM.nextInt(ALL_CHARACTERS.length())));
        }

        return password.toString();
    }
}