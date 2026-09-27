/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projekthazi3;

/**
 *
 * @author boldi
 */
public class Employee extends Person {

    // Konstans: a cégben maximum ennyi Employee lehet.
    public static final int MAX_EMPLOYEES = 20;

    // Static számláló: hány Employee-t hoztunk létre eddig összesen
    // (a Managerek is beleszámítanak, mert Manager extends Employee).
    private static int employeeCount = 0;

    protected int employeeId;
    protected double salary;

    public Employee(int employeeId, String name, String birthday, String cnp, double salary) {
        super(name, birthday, cnp);

        // Egyszerű ellenőrzés: ha elértük a maximumot, ne engedjük
        // létrehozni az új Employee-t.
        if (employeeCount >= MAX_EMPLOYEES) {
            throw new IllegalStateException("Elerte a maximum letszamot (" + MAX_EMPLOYEES + ")!");
        }

        this.employeeId = employeeId;
        this.salary = salary;
        employeeCount++;
    }

    public static int getEmployeeCount() {
        return employeeCount;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
