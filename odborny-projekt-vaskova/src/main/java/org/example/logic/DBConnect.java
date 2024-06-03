package org.example.logic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnect {
    private static String url;
    private Connection connection;

    public DBConnect() {
        connection = connect();
    }

    public Connection connect() {
        url = "jdbc:sqlite::resource:" + getClass().getResource("/CompanyProgram.db");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            return conn;
        }
    }

    public void executeSQL(String sql) {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addUser(String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO Users(name, email, password) VALUES(?,?,?)";

        try (
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            pstmt.executeUpdate();
        }
    }

    public List<ChecklistItem> getItemsFromTable(String tableName) {
        String sql = "SELECT id, Item FROM " + tableName;
        List<ChecklistItem> items = new ArrayList<>();
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String label = rs.getString("Item");
                items.add(new ChecklistItem(id, label));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return items;
    }

    public void addItemInTable(String lastUsedTable, String itemToAdd) {
        String sql = "INSERT INTO " + lastUsedTable + "(Item) VALUES('" + itemToAdd + "')";
        executeSQL(sql);
    }

    public void removeItemById(String lastUsedTable, int id) throws SQLException {
        String sql = "DELETE FROM " + lastUsedTable + " WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public String findPasswordFromUsername(String username) {
        String password = null;
        String sql = "SELECT password FROM Users WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                password = rs.getString("password");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return password;
    }
}