/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projekthazi3;

/**
 *
 * @author boldi
 */

public class Main {

    public static void main(String[] args) {

        System.out.println("=== 1. Managerek letrehozasa ===");
        Manager manager1 = new Manager(1, "Kovacs Anna", "2000-01-10", "2000101123456", 3000.0, "Sales");
        Manager manager2 = new Manager(2, "Nagy Bela", "1998-05-05", "1998050512345", 3500.0, "IT");
        Manager manager3 = new Manager(3, "Szabo Reka", "2001-03-03", "2001030354321", 4000.0, "HR");

        System.out.println();
        System.out.println("=== 2. Manager count kiirasa ===");
        System.out.println("Number of managers: " + Manager.getManagerCount());
        System.out.println("Osszes Employee (Manager is beleszamit): " + Employee.getEmployeeCount());

        System.out.println();
        System.out.println("=== 3. Singleton Director lekerese ===");
        Director d1 = Director.getInstance();
        Director d2 = Director.getInstance();
        // d1 es d2 UGYANARRA a peldanyra mutat:
        System.out.println("d1 == d2 ? " + (d1 == d2));

        System.out.println();
        System.out.println("=== 4. Managerek hozzaadasa a Directorhoz ===");
        d1.addManager(manager1);
        d1.addManager(manager2);
        d1.addManager(manager3);

        System.out.println();
        System.out.println("=== 5. Manager lista hasznalata ===");
        for (Manager m : d1.getManagers()) {
            System.out.println("- " + m.getName() + " (" + m.getDepartment() + "), fizetes: " + m.getSalary());
        }

        System.out.println();
        System.out.println("=== 6. Manager count kiirasa ujra ===");
        System.out.println("Number of managers: " + Director.getInstance().getManagers().size());

        System.out.println();
        System.out.println("=== 7. Teljes ceg fizetesenek kiszamitasa ===");
        System.out.println("Total salary: " + d2.getTotalSalary());

        System.out.println();
        System.out.println("=== 8. MAX_EMPLOYEES limit kiprobalasa try-catch-csel ===");
        System.out.println("MAX_EMPLOYEES = " + Employee.MAX_EMPLOYEES);
        try {
            // Mesterségesen "kitöltjük" a helyeket, hogy lássuk, mi történik,
            // ha elérjük a limitet. Már létrehoztunk 3 Employee-t (a 3 Managert),
            // tehát MAX_EMPLOYEES - 3 db sima Employee-t hozunk létre.
            for (int i = Employee.getEmployeeCount(); i < Employee.MAX_EMPLOYEES; i++) {
                new Employee(100 + i, "Teszt Dolgozo " + i, "1990-01-01", "1990010112345", 3000.0);
            }
            System.out.println("Sikeresen letrehoztunk " + Employee.getEmployeeCount() + " Employee-t.");

            // Ez a 21. Employee mar tullepne a limitet -> kivetel dobodik:
            new Employee(999, "Extra Dolgozo", "1990-01-01", "1990010112345", 3000.0);

        } catch (IllegalStateException e) {
            System.out.println("Hiba: nem lehet uj Employee-t letrehozni -> " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Program vege ===");
    }
}
