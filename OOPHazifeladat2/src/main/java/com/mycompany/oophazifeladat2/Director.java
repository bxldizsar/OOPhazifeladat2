package com.mycompany.oophazifeladat2;

import java.util.ArrayList;
import java.util.List;

// Director SINGLETON: a programban csak EGY Director objektum lehet.
public class Director {
    // Konstans: a ceg maximum 20 employee-t tartalmazhat
    public static final int MAX_EMPLOYEES = 20;

    // 1) static valtozo, ez tarolja az egyetlen peldanyt
    private static Director instance;

    private List<Manager> managers;

    // 2) PRIVATE konstruktor: kivulrol nem lehet "new Director()"-t irni
    private Director() {
        managers = new ArrayList<Manager>();
    }

    // 3) Ezen keresztul kerjuk el a Directort.
    //    Ha meg nincs, letrehozza; ha mar van, ugyanazt adja vissza.
    public static Director getInstance() {
        if (instance == null) {
            instance = new Director();
        }
        return instance;
    }

    public void addManager(Manager manager) {
        managers.add(manager);
    }

    public int getManagerCount() {
        return managers.size();
    }

    public List<Manager> getManagers() {
        return managers;
    }

    // a managerek osszes fizetese
    public double getTotalSalary() {
        double total = 0;
        for (Manager m : managers) {
            total += m.getSalary();
        }
        return total;
    }

    // Manager keresese id alapjan (null, ha nincs ilyen)
    public Manager findManagerById(int id) {
        for (Manager m : managers) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    // Manager eltavolitasa a listabol (torles / anonimizalas utan)
    public void removeManagerById(int id) {
        Manager m = findManagerById(id);
        if (m != null) {
            managers.remove(m);
            Manager.decreaseManagerCount();
        }
    }
}