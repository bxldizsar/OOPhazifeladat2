package com.mycompany.oophazifeladat2;

import java.util.ArrayList;
import java.util.Scanner;

// A program "logikaja": regisztracio, bejelentkezes es a szerepkorok menui.
// SQL itt NINCS, minden adatbazis-muveletet a DatabaseManager vegez.
public class UserService {
    private Director director = Director.getInstance();

    // ================= INDULAS =================
    public void init() {
        DatabaseManager.init();
        // a meglevo Managereket betoltjuk a Director (Singleton) listajaba
        ArrayList<Manager> managers = DatabaseManager.loadManagers();
        for (Manager m : managers) {
            director.addManager(m);
        }
        System.out.println("Managers loaded: " + director.getManagerCount());
    }

    // ================= REGISZTRACIO =================
    public void register(Scanner sc) {
        // maximum 20 felhasznalo
        if (DatabaseManager.countUsers() >= Director.MAX_EMPLOYEES) {
            System.out.println("Cannot register: maximum " + Director.MAX_EMPLOYEES + " employees reached.");
            return;
        }

        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        if (!StringUtils.isValidUsername(username)) {
            System.out.println("Invalid username (3-20 characters: letters, digits, _).");
            return;
        }
        if (DatabaseManager.usernameExists(username)) {
            System.out.println("Username already exists.");
            return;
        }
        System.out.print("Password: ");
        String password = sc.nextLine();
        if (StringUtils.isNullOrEmpty(password)) {
            System.out.println("Password cannot be empty.");
            return;
        }
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("CNP: ");
        String cnp = sc.nextLine().trim();
        System.out.print("Birthday (yyyy-MM-dd): ");
        String birthday = sc.nextLine().trim();
        double salary = StringUtils.readDouble(sc, "Salary: ");
        int position = StringUtils.readInt(sc, "Position (1 = Employee, 2 = Manager): ");

        // a jelszo SOHA nem kerul sima szovegkent az adatbazisba
        String encoded = Caesar.hashPassword(password);

        Employee user;
        try {
            if (position == 1) {
                user = new Employee(0, name, cnp, birthday, salary, username, encoded);
            } else if (position == 2) {
                int managed = StringUtils.readInt(sc, "Employees managed: ");
                user = new Manager(0, name, cnp, birthday, salary, username, encoded, managed);
            } else {
                System.out.println("Invalid position.");
                return;
            }
        } catch (IllegalArgumentException e) {
            // a Person / Employee / Manager konstruktor validacioja (pl. hibas CNP)
            System.out.println("Registration failed: " + e.getMessage());
            return;
        }

        int id = DatabaseManager.insertUser(user, user.getType());
        if (id > 0) {
            user.setId(id);
            if (user instanceof Manager) {
                director.addManager((Manager) user); // a Director listajaba is bekerul
            }
            System.out.println("Registration successful. Your ID: " + id);
        } else if (user instanceof Manager) {
            Manager.decreaseManagerCount(); // nem mentettuk el, ne szamoljuk
        }
    }

    // ================= BEJELENTKEZES =================
    public void login(Scanner sc) {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine();

        // ha nincs ilyen username, a stored null lesz, es a verify false-t ad
        String stored = DatabaseManager.getStoredPassword(username);
        if (!Caesar.verifyPassword(password, stored)) {
            System.out.println("Invalid credentials"); // nem aruljuk el, mi volt rossz
            return;
        }
        System.out.println("Login successful");

        int myId = DatabaseManager.getIdByUsername(username);
        String type = DatabaseManager.getType(myId);

        // a szerepkornek megfelelo menu nyilik meg
        if (type.equals("DIRECTOR")) {
            directorMenu(sc, myId);
        } else if (type.equals("MANAGER")) {
            managerMenu(sc, myId);
        } else {
            employeeMenu(sc, myId);
        }
    }

    // ================= DIRECTOR MENU =================
    private void directorMenu(Scanner sc, int myId) {
        while (true) {
            System.out.println();
            System.out.println("--- DIRECTOR MENU ---");
            System.out.println("1. List all users");
            System.out.println("2. Show managers");
            System.out.println("3. Show manager count");
            System.out.println("4. Show total company salary");
            System.out.println("5. Update user");
            System.out.println("6. Delete user");
            System.out.println("7. GDPR anonymization");
            System.out.println("8. Log out");
            int choice = StringUtils.readInt(sc, "Choose: ");

            if (choice == 1) {
                DatabaseManager.printUsers(null);
            } else if (choice == 2) {
                showManagers();
            } else if (choice == 3) {
                System.out.println("Number of managers: " + Manager.getManagerCount());
            } else if (choice == 4) {
                System.out.println("Total manager salary (Director): " + director.getTotalSalary());
                System.out.println("Total company salary (all users): " + DatabaseManager.getTotalSalary());
            } else if (choice == 5) {
                int id = chooseUser(sc, "DIRECTOR", myId);
                if (id > 0) updateUser(sc, id);
            } else if (choice == 6) {
                int id = chooseUser(sc, "DIRECTOR", myId);
                if (id > 0) deleteUser(id);
            } else if (choice == 7) {
                int id = chooseUser(sc, "DIRECTOR", myId);
                if (id > 0) anonymizeUser(id);
            } else if (choice == 8) {
                return;
            } else {
                System.out.println("No such option.");
            }
        }
    }

    // ================= MANAGER MENU =================
    private void managerMenu(Scanner sc, int myId) {
        while (true) {
            System.out.println();
            System.out.println("--- MANAGER MENU ---");
            System.out.println("1. List employees");
            System.out.println("2. Update employee");
            System.out.println("3. Delete employee");
            System.out.println("4. Change my password");
            System.out.println("5. Log out");
            int choice = StringUtils.readInt(sc, "Choose: ");

            if (choice == 1) {
                DatabaseManager.printUsers("EMPLOYEE");
            } else if (choice == 2) {
                int id = chooseUser(sc, "MANAGER", myId);
                if (id > 0) updateUser(sc, id);
            } else if (choice == 3) {
                int id = chooseUser(sc, "MANAGER", myId);
                if (id > 0) deleteUser(id);
            } else if (choice == 4) {
                changePassword(sc, myId);
            } else if (choice == 5) {
                return;
            } else {
                System.out.println("No such option.");
            }
        }
    }

    // ================= EMPLOYEE MENU =================
    private void employeeMenu(Scanner sc, int myId) {
        while (true) {
            System.out.println();
            System.out.println("--- EMPLOYEE MENU ---");
            System.out.println("1. Show my data");
            System.out.println("2. Change my password");
            System.out.println("3. Log out");
            int choice = StringUtils.readInt(sc, "Choose: ");

            if (choice == 1) {
                DatabaseManager.printUserById(myId);
            } else if (choice == 2) {
                changePassword(sc, myId);
            } else if (choice == 3) {
                return;
            } else {
                System.out.println("No such option.");
            }
        }
    }

    // ================= KOZOS MUVELETEK =================

    // Bekeri a modositando/torlendo felhasznalo id-jat es ellenorzi a jogosultsagot.
    // Visszaadja az id-t, vagy -1-et, ha nem szabad.
    private int chooseUser(Scanner sc, String myType, int myId) {
        int id = StringUtils.readInt(sc, "User ID: ");
        String targetType = DatabaseManager.getType(id);

        if (targetType == null) {
            System.out.println("User not found.");
            return -1;
        }
        if (id == myId) {
            System.out.println("You cannot do this with your own account here.");
            return -1;
        }
        // a Manager csak Employee-t kezelhet
        if (myType.equals("MANAGER") && !targetType.equals("EMPLOYEE")) {
            System.out.println("Managers can only manage employees.");
            return -1;
        }
        return id;
    }

    private void showManagers() {
        if (director.getManagerCount() == 0) {
            System.out.println("No managers.");
            return;
        }
        for (Manager m : director.getManagers()) {
            System.out.println("[" + m.getId() + "] " + m.getName() + " | username: " + m.getUsername()
                    + " | salary: " + m.getSalary() + " | employees managed: " + m.getEmployeesManaged());
        }
    }

    // Update: nev, fizetes, username vagy jelszo
    private void updateUser(Scanner sc, int id) {
        System.out.println("1. Name  2. Salary  3. Username  4. Password");
        int choice = StringUtils.readInt(sc, "Choose: ");
        Manager manager = director.findManagerById(id); // null, ha nem manager
        boolean ok = false;

        if (choice == 1) {
            System.out.print("New name: ");
            String name = sc.nextLine().trim();
            if (StringUtils.isNullOrEmpty(name)) {
                System.out.println("Name cannot be empty.");
                return;
            }
            ok = DatabaseManager.updateColumn(id, "name", name);
            if (ok && manager != null) manager.setName(name);
        } else if (choice == 2) {
            double salary = StringUtils.readDouble(sc, "New salary: ");
            if (salary < 0) {
                System.out.println("Salary cannot be negative.");
                return;
            }
            ok = DatabaseManager.updateColumn(id, "salary", String.valueOf(salary));
            if (ok && manager != null) manager.setSalary(salary);
        } else if (choice == 3) {
            System.out.print("New username: ");
            String username = sc.nextLine().trim();
            if (!StringUtils.isValidUsername(username) || DatabaseManager.usernameExists(username)) {
                System.out.println("Invalid or already used username.");
                return;
            }
            ok = DatabaseManager.updateColumn(id, "username", username);
            if (ok && manager != null) manager.setUsername(username);
        } else if (choice == 4) {
            changePassword(sc, id);
            return;
        } else {
            System.out.println("No such option.");
            return;
        }
        if (ok) {
            System.out.println("User updated.");
        }
    }

    // Jelszocsere: Caesar kodolas + mentes + uj timestamp (a DatabaseManager intezi)
    private void changePassword(Scanner sc, int id) {
        System.out.print("New password: ");
        String password = sc.nextLine();
        if (StringUtils.isNullOrEmpty(password)) {
            System.out.println("Password cannot be empty.");
            return;
        }
        String encoded = Caesar.hashPassword(password);
        if (DatabaseManager.updatePassword(id, encoded)) {
            Manager manager = director.findManagerById(id);
            if (manager != null) manager.setPassword(encoded);
            System.out.println("Password changed.");
        }
    }

    // GDPR delete
    private void deleteUser(int id) {
        if (DatabaseManager.deleteUser(id)) {
            director.removeManagerById(id); // ha manager volt, a Director listajabol is kikerul
            System.out.println("User deleted.");
        }
    }

    // GDPR anonymization
    private void anonymizeUser(int id) {
        if (DatabaseManager.anonymizeUser(id)) {
            director.removeManagerById(id);
            System.out.println("User anonymized.");
        }
    }
}