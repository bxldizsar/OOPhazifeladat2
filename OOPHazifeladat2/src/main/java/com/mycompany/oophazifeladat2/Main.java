package com.mycompany.oophazifeladat2;

public class Main {
    public static void main(String[] args) {

        DatabaseManager db = new DatabaseManager();
        db.createTables();

        // Caesar teszt
        CaesarHelper.testCaesar();

        // 1. Manager letrehozasa (username: boss, password: Titok123)
        Manager m = null;
        try {
            m = new Manager(1, "Kiss Bela", "1900101260017", "1990-01-01",
                    5000, "boss", "Titok123", 5);
        } catch (IllegalArgumentException e) {
            System.out.println("Hibas adat: " + e.getMessage());
            return;
        }

        // 2. Mentes (csak az ELSO futtatasnal kell, utana kommenteld ki)
        db.saveManager(m);

        // 3. Login: te gepeled be a username-et es a passwordot
        db.login();
    }
}