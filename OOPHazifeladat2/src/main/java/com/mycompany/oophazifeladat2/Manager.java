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

    public Manager(int id, String name, String cnp, String birthday, double salary,
                    String username, String password, int employeesManaged)
            // meghivja az employee konstruktort, az meg a personet
    {
        super(id, name, cnp, birthday, salary, username, password);

        if (employeesManaged < 0) {
            throw new IllegalArgumentException("Az employeek szama nem lehet negativ: " + employeesManaged);
        }
        this.employeesManaged = employeesManaged;
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
