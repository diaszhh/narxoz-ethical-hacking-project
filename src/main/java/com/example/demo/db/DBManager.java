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
        String sql = "SELECT id, name FROM persons";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                persons.add(new Person(
                        rs.getLong("id"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return persons;
    }

    // ❌ УЯЗВИМО: склейка строки
    public List<Person> findByNameVulnerable(String name) {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT id, name, password FROM persons WHERE name = '" + name + "' ORDER BY id";
        System.out.println("SQL: " + sql);

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                persons.add(new Person(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return persons;
    }

    // ✅ БЕЗОПАСНО
    public List<Person> findByNameSafe(String name) {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT id, name, password FROM persons WHERE name = ? ORDER BY id";
        System.out.println("SQL: " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    persons.add(new Person(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("password")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return persons;
    }

    // ❌ УЯЗВИМО: ORDER BY
    public List<Person> getAllPersons(String sort) {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT id, name, password FROM persons ORDER BY " + sort;
        System.out.println("SQL: " + sql);

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                persons.add(new Person(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return persons;
    }

    // ✅ БЕЗОПАСНО: allowlist
    public List<Person> getAllPersonsSafe(String sort) {
        if (!"id".equals(sort) && !"name".equals(sort) && !"password".equals(sort)) {
            sort = "id";
        }

        List<Person> persons = new ArrayList<>();
        String sql = "SELECT id, name, password FROM persons ORDER BY " + sort;
        System.out.println("SQL: " + sql);

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                persons.add(new Person(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return persons;
    }

    // ❌ УЯЗВИМО
    public Person createPersonVulnerable(Person person) {
        String sql = "INSERT INTO persons(name, password) VALUES ('"
                + person.getName() + "', '"
                + person.getPassword() + "')";

        System.out.println("SQL = " + sql);

        try (Statement st = connection.createStatement()) {
            st.executeUpdate(sql);

            try (ResultSet rs = st.executeQuery("SELECT id, name, password FROM persons ORDER BY id DESC LIMIT 1")) {
                if (rs.next()) {
                    return new Person(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return person;
    }

    // ✅ БЕЗОПАСНО
    public Person createPersonSafe(Person person) {
        String sql = "INSERT INTO persons(name, password) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, person.getName());
            ps.setString(2, person.getPassword());
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

    // ❌ УЯЗВИМО
    public List<Person> findByNameLikeVulnerable(String name) {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT id, name, password FROM persons WHERE name LIKE '%" + name + "%' ORDER BY id";
        System.out.println("SQL: " + sql);

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                persons.add(new Person(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return persons;
    }

    // ✅ БЕЗОПАСНО
    public List<Person> findByNameLikeSafe(String name) {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT id, name, password FROM persons WHERE name LIKE ? ORDER BY id";
        System.out.println("SQL: " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    persons.add(new Person(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("password")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return persons;
    }

    // ❌ УЯЗВИМО
    public Person findByIdVulnerable(String id) {
        String sql = "SELECT id, name, password FROM persons WHERE id = " + id;
        System.out.println("SQL: " + sql);

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return new Person(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    // ✅ БЕЗОПАСНО
    public Person findByIdSafe(Long id) {
        String sql = "SELECT id, name, password FROM persons WHERE id = ?";
        System.out.println("SQL: " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Person(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    // ❌ УЯЗВИМО
    public Person updatePersonVulnerable(String id, Person person) {
        String sql = "UPDATE persons SET name = '"
                + person.getName()
                + "', password = '"
                + person.getPassword()
                + "' WHERE id = " + id;

        System.out.println("SQL: " + sql);

        try (Statement st = connection.createStatement()) {
            st.executeUpdate(sql);

            try (ResultSet rs = st.executeQuery("SELECT id, name, password FROM persons WHERE id = " + id)) {
                if (rs.next()) {
                    return new Person(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    // ✅ БЕЗОПАСНО
    public Person updatePersonSafe(Long id, Person person) {
        String sql = "UPDATE persons SET name = ?, password = ? WHERE id = ?";
        System.out.println("SQL: " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, person.getName());
            ps.setString(2, person.getPassword());
            ps.setLong(3, id);
            ps.executeUpdate();

            try (PreparedStatement ps2 = connection.prepareStatement(
                    "SELECT id, name, password FROM persons WHERE id = ?")) {
                ps2.setLong(1, id);

                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        return new Person(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getString("password")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    // ❌ УЯЗВИМО
    public void deletePersonVulnerable(String id) {
        String sql = "DELETE FROM persons WHERE id = " + id;
        System.out.println("SQL: " + sql);

        try (Statement st = connection.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ✅ БЕЗОПАСНО
    public void deletePersonSafe(Long id) {
        String sql = "DELETE FROM persons WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ❌ УЯЗВИМО: логин через persons
    public Person loginVulnerable(String name, String password) {
        String sql = "SELECT id, name, password FROM persons WHERE name = '" + name +
                "' AND password = '" + password + "'";
        System.out.println("SQL: " + sql);

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return new Person(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


    // ✅ БЕЗОПАСНО: PreparedStatement
    public Person loginSafe(String name, String password) {
        String sql = "SELECT id, name, password FROM persons WHERE name = ? AND password = ?";
        System.out.println("SQL: " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Person(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}