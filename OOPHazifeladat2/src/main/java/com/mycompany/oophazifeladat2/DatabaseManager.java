package com.mycompany.oophazifeladat2;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

// Itt van MINDEN, ami SQL: tabla letrehozasa, CSV import, insert, select, update, delete.
// A tobbi osztaly nem ir SQL-t, csak ezeket a metodusokat hivja.
public class DatabaseManager {
    private static final String DB_FILE = "company.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;
    private static final String CSV_FILE = "users.csv";

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Eroforrasok lezarasa (minden finally blokkbol ezt hivjuk)
    private static void close(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Error while closing: " + e.getMessage());
        }
    }

    // ================= INDULAS =================

    // Ha nincs adatbazis vagy tabla, letrehozzuk. Ha a tabla ures, beolvassuk a users.csv-t.
    public static void init() {
        if (new File(DB_FILE).exists()) {
            System.out.println("Database found: " + DB_FILE);
        } else {
            System.out.println("Database not found, creating: " + DB_FILE);
        }

        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "cnp TEXT, "
                + "birthday TEXT, "
                + "type TEXT NOT NULL, "            // EMPLOYEE / MANAGER / DIRECTOR
                + "salary REAL, "
                + "employees_managed INTEGER, "
                + "username TEXT UNIQUE, "
                + "password TEXT, "
                + "password_timestamp TEXT)";

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = connect();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            close(null, stmt, conn);
        }

        if (countUsers() == 0) {
            importFromCsv();
        }
    }

    // Kezdo adatok beolvasasa fajlbol (csak az elso inditaskor, amikor ures a tabla).
    // Formatum: type;name;cnp;birthday;salary;employees_managed;username;password
    private static void importFromCsv() {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            System.out.println("No " + CSV_FILE + " found, starting with an empty database.");
            return;
        }

        Scanner sc = null;
        int imported = 0;
        try {
            sc = new Scanner(file);
            if (sc.hasNextLine()) {
                sc.nextLine(); // az elso sor a fejlec, kihagyjuk
            }
            int lineNumber = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                lineNumber++;
                if (line.length() == 0) {
                    continue;
                }
                try {
                    String[] p = line.split(";");
                    if (p.length != 8) {
                        throw new IllegalArgumentException("8 mezo kell, " + p.length + " van");
                    }
                    String type = p[0].trim();
                    double salary = Double.parseDouble(p[4].trim());   // itt dobhat NumberFormatException-t
                    int managed = Integer.parseInt(p[5].trim());
                    String encoded = Caesar.hashPassword(p[7].trim()); // a CSV-ben sima jelszo van

                    // A konstruktor ellenorzi a CNP-t es a szuletesnapot
                    Employee user;
                    if (type.equals("MANAGER")) {
                        user = new Manager(0, p[1], p[2], p[3], salary, p[6], encoded, managed);
                    } else if (type.equals("EMPLOYEE") || type.equals("DIRECTOR")) {
                        user = new Employee(0, p[1], p[2], p[3], salary, p[6], encoded);
                    } else {
                        throw new IllegalArgumentException("ismeretlen tipus: " + type);
                    }

                    if (insertUser(user, type) > 0) {
                        imported++;
                    }
                    if (user instanceof Manager) {
                        Manager.decreaseManagerCount(); // import kozben meg nem szamoljuk, kesobb betoltjuk
                    }
                } catch (NumberFormatException e) {
                    System.out.println("CSV line " + lineNumber + " skipped (not a number): " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("CSV line " + lineNumber + " skipped: " + e.getMessage());
                }
            }
            System.out.println("Imported " + imported + " users from " + CSV_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("File error: " + e.getMessage());
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }

    // Managerek betoltese (a Director listajahoz). Az anonimizaltakat kihagyjuk.
    public static ArrayList<Manager> loadManagers() {
        ArrayList<Manager> managers = new ArrayList<Manager>();
        String sql = "SELECT * FROM users WHERE type = 'MANAGER' AND cnp <> 'ANONYMOUS'";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Manager m = new Manager(rs.getInt("id"), rs.getString("name"), rs.getString("cnp"),
                        rs.getString("birthday"), rs.getDouble("salary"), rs.getString("username"),
                        rs.getString("password"), rs.getInt("employees_managed"));
                m.setPasswordTimestamp(rs.getString("password_timestamp"));
                managers.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            close(rs, stmt, conn);
        }
        return managers;
    }

    // ================= INSERT =================

    // Uj felhasznalo mentese. Visszaadja az uj id-t (-1, ha nem sikerult).
    public static int insertUser(Employee user, String type) {
        String sql = "INSERT INTO users (name, cnp, birthday, type, salary, employees_managed, "
                + "username, password, password_timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int managed = 0;
        if (user instanceof Manager) {
            managed = ((Manager) user).getEmployeesManaged();
        }

        Connection conn = null;
        PreparedStatement ps = null;
        Statement stmt = null;
        ResultSet rs = null;
        int newId = -1;
        try {
            conn = connect();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getName());
            ps.setString(2, user.getCnp());
            ps.setString(3, user.getBirthday());
            ps.setString(4, type);
            ps.setDouble(5, user.getSalary());
            ps.setInt(6, managed);
            ps.setString(7, user.getUsername());
            ps.setString(8, user.getPassword());
            ps.setString(9, LocalDateTime.now().toString()); // password timestamp
            ps.executeUpdate();

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT last_insert_rowid()"); // az uj sor id-ja
            if (rs.next()) {
                newId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            close(rs, stmt, null);
            close(null, ps, conn);
        }
        return newId;
    }

    // ================= LEKERDEZESEK =================

    // Visszaadja a felhasznalo tarolt (kodolt) jelszavat, vagy null-t, ha nincs ilyen username
    public static String getStoredPassword(String username) {
        return getText("SELECT password FROM users WHERE username = ?", username);
    }

    public static int getIdByUsername(String username) {
        String id = getText("SELECT id FROM users WHERE username = ?", username);
        return id == null ? -1 : Integer.parseInt(id);
    }

    // EMPLOYEE / MANAGER / DIRECTOR, vagy null, ha nincs ilyen id
    public static String getType(int id) {
        return getText("SELECT type FROM users WHERE id = ?", String.valueOf(id));
    }

    public static boolean usernameExists(String username) {
        return getIdByUsername(username) != -1;
    }

    public static int countUsers() {
        return (int) getNumber("SELECT COUNT(*) FROM users");
    }

    public static double getTotalSalary() {
        return getNumber("SELECT SUM(salary) FROM users");
    }

    // Felhasznalok kiirasa. type == null -> mindenki, kulonben csak az adott tipus
    public static void printUsers(String type) {
        String sql = "SELECT * FROM users";
        if (type != null) {
            sql += " WHERE type = ?";
        }
        sql += " ORDER BY type, id";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = connect();
            ps = conn.prepareStatement(sql);
            if (type != null) {
                ps.setString(1, type);
            }
            rs = ps.executeQuery();
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.println("[" + rs.getInt("id") + "] " + rs.getString("type")
                        + " | " + rs.getString("name")
                        + " | born: " + rs.getString("birthday")
                        + " | username: " + rs.getString("username")
                        + " | salary: " + rs.getDouble("salary"));
            }
            if (!any) {
                System.out.println("No users found.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            close(rs, ps, conn);
        }
    }

    // Egy felhasznalo adatainak kiirasa id alapjan
    public static void printUserById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = connect();
            ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Name:      " + rs.getString("name"));
                System.out.println("CNP:       " + rs.getString("cnp"));
                System.out.println("Birthday:  " + rs.getString("birthday"));
                System.out.println("Type:      " + rs.getString("type"));
                System.out.println("Salary:    " + rs.getDouble("salary"));
                System.out.println("Username:  " + rs.getString("username"));
                System.out.println("Password last changed: " + rs.getString("password_timestamp"));
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            close(rs, ps, conn);
        }
    }

    // Egy szoveges erteket ad vissza egy lekerdezesbol (1 parameterrel)
    private static String getText(String sql, String param) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String result = null;
        try {
            conn = connect();
            ps = conn.prepareStatement(sql);
            ps.setString(1, param);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString(1);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            close(rs, ps, conn);
        }
        return result;
    }

    // Egy szamot ad vissza (COUNT, SUM)
    private static double getNumber(String sql) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        double result = 0;
        try {
            conn = connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                result = rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            close(rs, stmt, conn);
        }
        return result;
    }

    // ================= UPDATE / DELETE / GDPR =================

    // Egy oszlop modositasa (az oszlop nevet a kod adja, nem a felhasznalo)
    public static boolean updateColumn(int id, String column, String value) {
        return runUpdate("UPDATE users SET " + column + " = ? WHERE id = ?", value, id);
    }

    // Uj (kodolt) jelszo + UJ timestamp
    public static boolean updatePassword(int id, String encodedPassword) {
        boolean ok = runUpdate("UPDATE users SET password = ? WHERE id = ?", encodedPassword, id);
        if (ok) {
            updateColumn(id, "password_timestamp", LocalDateTime.now().toString());
        }
        return ok;
    }

    // GDPR delete: a sor teljesen eltunik
    public static boolean deleteUser(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connect();
            ps = conn.prepareStatement("DELETE FROM users WHERE id = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        } finally {
            close(null, ps, conn);
        }
    }

    // GDPR anonimizalas: a szemelyes adat eltunik, az id, type, salary megmarad
    public static boolean anonymizeUser(int id) {
        String sql = "UPDATE users SET name = 'ANONYMOUS', cnp = 'ANONYMOUS', birthday = 'ANONYMOUS', "
                + "username = ?, password = NULL, password_timestamp = NULL WHERE id = ?";
        return runUpdate(sql, "anonymous_" + id, id);
    }

    // UPDATE/DELETE futtatasa: 1. parameter szoveg, 2. parameter id. true, ha modositott sort.
    private static boolean runUpdate(String sql, String value, int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connect();
            ps = conn.prepareStatement(sql);
            ps.setString(1, value);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        } finally {
            close(null, ps, conn);
        }
    }
}