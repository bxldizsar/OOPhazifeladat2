package com.mycompany.oophazifeladat2;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:company.db";

    // Kapcsolat megnyitása
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Erőforrások lezárása (a finally blokkokból hívjuk)
    private void closeAll(Connection conn, Statement st, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Hiba a lezáráskor: " + e.getMessage());
        }
    }

    // ---------- TÁBLA LÉTREHOZÁSA ----------
    public void createTables() {
        Connection conn = null;
        Statement st = null;
        String sql = "CREATE TABLE IF NOT EXISTS person ("
                + "id INTEGER PRIMARY KEY, "
                + "type TEXT NOT NULL, "
                + "name TEXT, "
                + "cnp TEXT, "
                + "birthday TEXT, "
                + "salary REAL, "
                + "employees_managed INTEGER, "
                + "username TEXT UNIQUE, "
                + "password TEXT, "
                + "password_timestamp TEXT, "
                + "anonymized INTEGER DEFAULT 0)";
        try {
            conn = connect();
            st = conn.createStatement();
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println("Hiba a tábla létrehozásakor: " + e.getMessage());
        } finally {
            closeAll(conn, st, null);
        }
    }

    // ---------- MANAGER MENTÉSE ----------
    public void saveManager(Manager m) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO person (id, type, name, cnp, birthday, salary, "
                + "employees_managed, username, password, password_timestamp) "
                + "VALUES (?, 'MANAGER', ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conn = connect();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, m.getId());
            ps.setString(2, m.getName());
            ps.setString(3, m.getCnp());
            ps.setString(4, m.getBirthday());
            ps.setDouble(5, m.getSalary());
            ps.setInt(6, m.getEmployeesManaged());
            ps.setString(7, m.getUsername());
            // A jelszót KÓDOLVA mentjük, nem sima szövegként
            ps.setString(8, CaesarHelper.caesarEncode(m.getPassword()));
            // Timestamp: mikor mentettük a jelszót
            ps.setString(9, LocalDateTime.now().toString());
            ps.executeUpdate();
            System.out.println("Manager elmentve.");
        } catch (SQLException e) {
            System.out.println("Hiba a mentéskor: " + e.getMessage());
        } finally {
            closeAll(conn, ps, null);
        }
    }

    // ---------- JELSZÓ ELLENŐRZÉSE ----------
    public boolean verifyPassword(String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean ok = false;
        try {
            conn = connect();
            ps = conn.prepareStatement("SELECT password FROM person WHERE username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // A megadott jelszót is kódoljuk, és a két kódolt szöveget hasonlítjuk össze
                if (storedPassword != null
                        && storedPassword.equals(CaesarHelper.caesarEncode(password))) {
                    ok = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Hiba az ellenőrzéskor: " + e.getMessage());
        } finally {
            closeAll(conn, ps, rs);
        }
        return ok;
    }

    // ---------- LOGIN ----------
    // Billentyűzetről kéri be az adatokat
    public boolean login() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        return login(username, password);
    }

    // A tényleges ellenőrzés (a demóban ezt hívjuk fix adatokkal)
    public boolean login(String username, String password) {
        if (verifyPassword(username, password)) {
            System.out.println("Login successful");
            return true;
        } else {
            // Hibás username VAGY password esetén ugyanaz az üzenet
            System.out.println("Invalid credentials");
            return false;
        }
    }

    // ---------- UPDATE: név és fizetés ----------
    public void updatePerson(int id, String newName, double newSalary) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connect();
            ps = conn.prepareStatement("UPDATE person SET name = ?, salary = ? WHERE id = ?");
            ps.setString(1, newName);
            ps.setDouble(2, newSalary);
            ps.setInt(3, id);
            int rows = ps.executeUpdate();
            System.out.println(rows + " sor módosítva.");
        } catch (SQLException e) {
            System.out.println("Hiba a módosításkor: " + e.getMessage());
        } finally {
            closeAll(conn, ps, null);
        }
    }

    // ---------- UPDATE: jelszócsere (a timestamp is frissül) ----------
    public void updatePassword(int id, String newPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connect();
            ps = conn.prepareStatement(
                "UPDATE person SET password = ?, password_timestamp = ? WHERE id = ?");
            ps.setString(1, CaesarHelper.caesarEncode(newPassword));
            ps.setString(2, LocalDateTime.now().toString());
            ps.setInt(3, id);
            ps.executeUpdate();
            System.out.println("Jelszó módosítva.");
        } catch (SQLException e) {
            System.out.println("Hiba a jelszócserénél: " + e.getMessage());
        } finally {
            closeAll(conn, ps, null);
        }
    }

    // ---------- TÖRLÉS ----------
    public void deletePerson(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connect();
            ps = conn.prepareStatement("DELETE FROM person WHERE id = ?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows + " sor törölve.");
        } catch (SQLException e) {
            System.out.println("Hiba a törléskor: " + e.getMessage());
        } finally {
            closeAll(conn, ps, null);
        }
    }

    // ---------- ANONIMIZÁLÁS ----------
    public void anonymizePerson(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "UPDATE person SET name = 'ANONYMOUS', cnp = NULL, birthday = NULL, "
                + "username = 'anon_' || id, password = NULL, password_timestamp = NULL, "
                + "anonymized = 1 WHERE id = ?";
        try {
            conn = connect();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows + " sor anonimizálva.");
        } catch (SQLException e) {
            System.out.println("Hiba az anonimizáláskor: " + e.getMessage());
        } finally {
            closeAll(conn, ps, null);
        }
    }
}
