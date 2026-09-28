/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oophazifeladat2;

/**
 *
 * @author boldi
 */
public class Employee extends Person {

    private int id;
    private double salary;
    private String username;
    private String password; //  ide mar a Caesar-kodolt jelszot adjuk at,
                             // sima szovegkent sehol nem taroljuk a jelszot
    private String passwordTimestamp; // mikor lett a jelszo beallitva (feladat 14. pont)

    // a konstruktor elso dolga, hogy meghivja a Person konstruktorat
    // nekunk itt mar tobb uj mezot is ellenorizni/beallitani kell
    public Employee(int id, String name, String cnp, String birthday,
                     double salary, String username, String password) {
        super(name, cnp, birthday); // meghivja a Person(name, cnp, birthday) konstruktorat

        if (salary < 0) {
            throw new IllegalArgumentException("A fizetes nem lehet negativ: " + salary);
        }

        this.id = id;
        this.salary = salary;
        this.username = username;
        this.password = password;
    }

    // UJ: a Person abstract metodusanak megirasa
    @Override
    public String getType() {
        return "EMPLOYEE";
    }

    // Getter es Setter, az uj mezokhoz, a person gettereit atvette

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordTimestamp() {
        return passwordTimestamp;
    }

    public void setPasswordTimestamp(String passwordTimestamp) {
        this.passwordTimestamp = passwordTimestamp;
    }
}