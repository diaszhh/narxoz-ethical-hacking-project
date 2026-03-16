package com.example.demo.db;

import com.example.demo.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private static final String URL = "jdbc:postgresql://localhost:5432/demo_db5";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private final Connection connection;

    public DBManager() {
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("DB connection failed", e);
        }
    }

    public List<Person> getAllPersons() {
        List<Person> persons = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, name FROM persons")) {

            while (rs.next()) {
                persons.add(new Person(rs.getLong("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return persons;


    }

    // ❌ УЯЗВИМО: склейка строки
    public List<Person> findByNameVulnerable(String name) {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT id, name FROM persons WHERE name = '" + name + "' ORDER BY id";
        System.out.println("SQL: " + sql);

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                persons.add(new Person(rs.getLong("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return persons;
    }

    // ✅ БЕЗОПАСНО: параметризация
    public List<Person> findByNameSafe(String name) {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT id, name FROM persons WHERE name = ? ORDER BY id";
        System.out.println("SQL: " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    persons.add(new Person(rs.getLong("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return persons;
    }

    public List<Person> getAllPersons(String sort) {

        List<Person> persons = new ArrayList<>();

        String sql = "SELECT id, name FROM persons ORDER BY " + sort;
        System.out.println("SQL: " + sql);
        try {
//            SELECT id, name FROM persons ORDER BY id desc;
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                persons.add(
                        new Person(
                                rs.getLong("id"),
                                rs.getString("name")
                        )
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return persons;
    }

    public List<Person> getAllPersonsSafe(String sort) {

        if (!sort.equals("id") && !sort.equals("name")) {
            sort = "id";
        }

        String sql = "SELECT id, name FROM persons ORDER BY " + sort;

        List<Person> persons = new ArrayList<>();


        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                persons.add(new Person(rs.getLong("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return persons;
    }

    // ❌ УЯЗВИМО: склейка строки (делать так нельзя)
    public Person createPersonVulnerable(Person person) {

        String sql = "INSERT INTO persons(name) VALUES ('" + person.getName() + "')";


        try (Statement st = connection.createStatement()) {
            System.out.println("SQL = " + sql);
            st.executeUpdate(sql);

            // чтобы вернуть id — просто достанем последнюю вставку (для демо)
            try (ResultSet rs = st.executeQuery("SELECT id, name FROM persons ORDER BY id DESC LIMIT 1")) {
                if (rs.next()) {
                    return new Person(rs.getLong("id"), rs.getString("name"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return person;
    }

    // ✅ БЕЗОПАСНО: PreparedStatement + возврат id
    public Person createPersonSafe(Person person) {

        String sql = "INSERT INTO persons(name) VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, person.getName());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    person.setId(keys.getLong(1));
                }
            }

            return person;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}