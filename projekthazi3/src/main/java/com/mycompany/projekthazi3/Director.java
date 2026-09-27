/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projekthazi3;

/**
 *
 * @author boldi
 */

import java.util.ArrayList;

public class Director {

    // Az EGYETLEN Director példány. Kezdetben null (még nem létezik).
    private static Director instance;

    private ArrayList<Manager> managers;

    // A konstruktor PRIVATE: kívülről senki nem hívhatja a "new Director()"-t.
    private Director() {
        managers = new ArrayList<>();
    }

    // Ez adja vissza mindig UGYANAZT a példányt.
    public static Director getInstance() {
        if (instance == null) {
            instance = new Director();
        }
        return instance;
    }

    public void addManager(Manager manager) {
        managers.add(manager);
    }

    public ArrayList<Manager> getManagers() {
        return managers;
    }

    // Egyszerű "for" ciklussal összeadjuk a Managerek fizetését.
    public double getTotalSalary() {
        double total = 0;
        for (Manager m : managers) {
            total = total + m.getSalary();
        }
        return total;
    }
}
