package com.mycompany.oophazifeladat2;

import java.util.Scanner;


public class Caesar {
    private static final int SHIFT = 3;

    // Jelszo kodolasa: minden betut es szamjegyet +3-mal eltol
    public static String hashPassword(String password) {
        return shift(password, SHIFT);
    }

    // Visszafejtes: ugyanaz, csak -3-mal
    public static String decryptPassword(String encryptedPassword) {
        return shift(encryptedPassword, -SHIFT);
    }

    // A beirt jelszot kodoljuk, es osszehasonlitjuk a tarolt kodolt jelszoval
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        return hashPassword(password).equals(storedHash);
    }

    // Karakterenkenti eltolas. A "+ 26" / "+ 10" azert kell, hogy visszafele
    // (negativ eltolasnal) se legyen negativ a maradek (pl. 'a' - 3 -> 'x').
    private static String shift(String text, int shift) {
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'a' && c <= 'z') {
                c = (char) ('a' + (c - 'a' + shift + 26) % 26);
            } else if (c >= 'A' && c <= 'Z') {
                c = (char) ('A' + (c - 'A' + shift + 26) % 26);
            } else if (c >= '0' && c <= '9') {
                c = (char) ('0' + (c - '0' + shift + 10) % 10);
            }
            // minden mas karakter (pl. !, @) valtozatlan marad
            result += c;
        }
        return result;
    }

    // Menubol hivhato teszt
    public static void testCaesar(Scanner sc) {
        System.out.println("Enter password:");
        String original = sc.nextLine();
        String encoded = hashPassword(original);
        String decoded = decryptPassword(encoded);
        System.out.println("Original: " + original);
        System.out.println("Encoded:  " + encoded);
        System.out.println("Decoded:  " + decoded);
    }
}