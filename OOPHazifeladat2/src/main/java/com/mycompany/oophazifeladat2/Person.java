/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oophazifeladat2;

/**
 *
 * @author boldi
 */

// csak Employee vagy Manager objektumot.
public abstract class Person {
    // A szemely adatai. Privatak, kivulrol csak getterrel/setterrel erjuk el oket.
    private String name;
    private String cnp;
    private String birthday; // formatum: yyyy-MM-dd, pl. 1990-01-01

    // KONSTRUKTOR: itt ellenorizzuk az adatokat.
    // Ha valami hibas, kivetelt dobunk, es a Person objektum nem jon letre.
    public Person(String name, String cnp, String birthday) {

        // 1. A nev ne legyen ures
        if (name.equals("")) {
            throw new IllegalArgumentException("A nev nem lehet ures!");
        }

        // 2. A CNP legyen jo (a validCnp metodus ellenorzi, lejjebb van)
        if (!validCnp(cnp)) {
            throw new IllegalArgumentException("Hibas CNP: " + cnp);
        }

        // 3. A szuletesi datum legyen jo (a validBirthday metodus ellenorzi, lejjebb van)
        if (!validBirthday(cnp, birthday)) {
            throw new IllegalArgumentException("Hibas szuletesi datum: " + birthday);
        }

        // Ha minden jo, beallitjuk az adatokat
        this.name = name;
        this.cnp = cnp;
        this.birthday = birthday;
    }

    // UJ: abstract metodus, minden gyerekosztaly megirja ("EMPLOYEE" / "MANAGER").
    // Ezt mentjuk el a "type" mezobe.
    public abstract String getType();

    // CNP ellenorzes. true = jo a CNP, false = rossz
    private boolean validCnp(String cnp) {

        // Pontosan 13 karakter legyen
        if (cnp.length() != 13) {
            return false;
        }

        // Mind a 13 karakter szamjegy legyen
        for (int i = 0; i < 13; i++) {
            char c = cnp.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }

        // Az utolso szamjegy az ellenorzo szam. Ezt az elso 12 szamjegybol
        // ki lehet szamolni a hivatalos kulccsal.
        String key = "279146358279";
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            // minden szamjegyet megszorzunk a kulcs megfelelo szamjegyevel, es osszeadjuk
            sum = sum + (cnp.charAt(i) - '0') * (key.charAt(i) - '0');
        }

        // Az osszeg maradeka 11-gyel osztva az ellenorzo szam (a 10 helyett 1 lesz)
        int control = sum % 11;
        if (control == 10) {
            control = 1;
        }

        // Egyezik a kiszamolt szam a CNP utolso szamjegyevel?
        return control == cnp.charAt(12) - '0';
    }

    // Szuletesi datum ellenorzes: egyezzen a CNP-ben levo datummal
    private boolean validBirthday(String cnp, String birthday) {

        // A CNP elso szamjegye: nem + evszazad (1,2 = 1900-as evek; 5,6 = 2000-es evek)
        int s = cnp.charAt(0) - '0';

        // A CNP-bol kivagjuk az evet, honapot, napot (szovegkent)
        String year = cnp.substring(1, 3);   // 2. es 3. karakter
        String month = cnp.substring(3, 5);  // 4. es 5. karakter
        String day = cnp.substring(5, 7);    // 6. es 7. karakter

        // Az evhez hozzarakjuk az evszazadot
        if (s == 1 || s == 2) {
            year = "19" + year;
        } else if (s == 5 || s == 6) {
            year = "20" + year;
        } else {
            return false;
        }

        // A honap 1-12, a nap 1-31 kozott legyen
        int m = Integer.parseInt(month);
        int d = Integer.parseInt(day);
        if (m < 1 || m > 12 || d < 1 || d > 31) {
            return false;
        }

        // Osszerakjuk a datumot yyyy-MM-dd formaban, es osszehasonlitjuk a megadottal
        return birthday.equals(year + "-" + month + "-" + day);
    }

    // GETTEREK: visszaadjak az adatot
    public String getName() {
        return name;
    }

    public String getCnp() {
        return cnp;
    }

    public String getBirthday() {
        return birthday;
    }

    // SETTEREK: beallitjak az adatot
    public void setName(String name) {
        this.name = name;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}