package com.example.util;

import java.security.SecureRandom;

public class ShortCodeGenerator {

    private static final String ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(int length) {
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            result.append(ALPHABET.charAt(index));
        }

        return result.toString();
    }
}
