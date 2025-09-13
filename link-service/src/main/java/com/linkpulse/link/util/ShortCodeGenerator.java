package com.linkpulse.link.util;

import java.security.SecureRandom;

public class ShortCodeGenerator {
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final SecureRandom RND = new SecureRandom();

    public String generate(int len) {
        char[] out = new char[len];
        for (int i = 0; i < len; i++) out[i] = ALPHABET[RND.nextInt(ALPHABET.length)];
        return new String(out);
    }
}
