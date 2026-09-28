/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oophazifeladat2;

/**
 *
 * @author boldi
 */

public class Manager extends Employee {

    private int employeesManaged;

    // STATIC szamlalo. A static valtozo az OSZTALYE, nem egy-egy objektume,
    // ezert minden Manager UGYANAZT az egy szamlalot latja es noveli.
    // Ha nem static lenne, minden Managernek sajat szamlaloja lenne (mindegyikben 1).
    private static int managerCount = 0;

    public Manager(int id, String name, String cnp, String birthday, double salary,
                    String username, String password, int employeesManaged)
            // meghivja az employee konstruktort, az meg a personet
    {
        super(id, name, cnp, birthday, salary, username, password);

        if (employeesManaged < 0) {
            throw new IllegalArgumentException("Az employeek szama nem lehet negativ: " + employeesManaged);
        }
        this.employeesManaged = employeesManaged;
        managerCount++; // UJ: minden uj Manager noveli a kozos szamlalot
    }

    // UJ: a Person abstract metodusanak felulirasa
    @Override
    public String getType() {
        return "MANAGER";
    }

    // UJ: static metodus, objektum nelkul hivhato: Manager.getManagerCount()
    public static int getManagerCount() {
        return managerCount;
    }

    // UJ: torles / anonimizalas utan csokkentjuk a szamlalot
    public static void decreaseManagerCount() {
        if (managerCount > 0) {
            managerCount--;
        }
    }

    // Getterek, Szetterek

    @Override
    public String toString() {
        return "Manager{" + "employeesManaged=" + employeesManaged + '}';
    }

    public int getEmployeesManaged() {
        return employeesManaged;
    }

    public void setEmployeesManaged(int employeesManaged) {
        this.employeesManaged = employeesManaged;
    }
}
