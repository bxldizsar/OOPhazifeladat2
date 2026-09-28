package com.mycompany.oophazifeladat2;

// Java-ban nincs nativ extension method (mint C#-ban a "this string s").
// Ezert egyszeru static helper/utility metodusokkal oldjuk meg:
//   C#:   username.IsValidUsername()
//   Java: StringUtils.isValidUsername(username)
public class StringUtils {

    public static boolean isNullOrEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

    // Ervenyes username: 3-20 karakter, csak betu, szam vagy _,
    // es nem kezdodhet "anonymous_"-szal (azt a GDPR anonimizalas hasznalja)
    public static boolean isValidUsername(String username) {
        if (isNullOrEmpty(username)) {
            return false;
        }
        if (username.length() < 3 || username.length() > 20) {
            return false;
        }
        if (username.startsWith("anonymous_")) {
            return false;
        }
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }
        return true;
    }

    // Egyszeru szam beolvasas: addig kerdez, amig nem szamot irnak be
    public static int readInt(java.util.Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    public static double readDouble(java.util.Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}