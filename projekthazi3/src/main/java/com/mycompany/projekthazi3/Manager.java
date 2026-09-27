/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projekthazi3;

/**
 *
 * @author boldi
 */


public class Manager extends Employee {

    // Static számláló: hány Managert hoztunk létre eddig összesen.
    private static int managerCount = 0;

    private String department;

    public Manager(int employeeId, String name, String birthday, String cnp,
                    double salary, String department) {
        super(employeeId, name, birthday, cnp, salary); // Employee konstruktora
        this.department = department;
        managerCount++; // minden uj Managernel no a szamlalo
    }

    // Static metódus: az osztályon keresztül hívható, nem kell hozzá objektum.
    public static int getManagerCount() {
        return managerCount;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
