package com.example.demo.controller;

import com.example.demo.db.DBManager;
import com.example.demo.model.Person;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonRestController {

    private final DBManager db = new DBManager();

    @GetMapping
    public List<Person> all() {
        return db.getAllPersons();
    }

    @GetMapping("/all-vuln")
    public List<Person> getAllSortVuln(@RequestParam String sort) {
        return db.getAllPersons(sort);
    }


    @GetMapping("/all-safe")
    public List<Person> getAllSortSafe(@RequestParam String sort) {
        return db.getAllPersonsSafe(sort);
    }

    // Уязвимый: GET /api/persons/search-vuln?name=...
    @GetMapping("/search-vuln")
    public List<Person> searchVuln(@RequestParam String name) {
        return db.findByNameVulnerable(name);
    }

    // Безопасный: GET /api/persons/search-safe?name=...
    @GetMapping("/search-safe")
    public List<Person> searchSafe(@RequestParam String name) {
        return db.findByNameSafe(name);
    }

    // Уязвимый: POST /api/persons/create-vuln
    @PostMapping("/create-vuln")
    public Person createVuln(@RequestBody Person person) {
        return db.createPersonVulnerable(person);
    }

    // Безопасный: POST /api/persons/create-safe
    @PostMapping("/create-safe")
    public Person createSafe(@RequestBody Person person) {
        return db.createPersonSafe(person);
    }

    @GetMapping("/search-like-vuln")
    public List<Person> searchLikeVuln(@RequestParam String name) {
        return db.findByNameLikeVulnerable(name);
    }

    @GetMapping("/search-like-safe")
    public List<Person> searchLikeSafe(@RequestParam String name) {
        return db.findByNameLikeSafe(name);
    }

    @GetMapping("/by-id-vuln")
    public Person getByIdVuln(@RequestParam String id) {
        return db.findByIdVulnerable(id);
    }

    @GetMapping("/by-id-safe")
    public Person getByIdSafe(@RequestParam Long id) {
        return db.findByIdSafe(id);
    }

    @PutMapping("/update-vuln/{id}")
    public Person updateVuln(@PathVariable String id, @RequestBody Person person) {
        return db.updatePersonVulnerable(id, person);
    }

    @PutMapping("/update-safe/{id}")
    public Person updateSafe(@PathVariable Long id, @RequestBody Person person) {
        return db.updatePersonSafe(id, person);
    }

    @DeleteMapping("/delete-vuln/{id}")
    public String deleteVuln(@PathVariable String id) {
        db.deletePersonVulnerable(id);
        return "Deleted vulnerable";
    }

    @DeleteMapping("/delete-safe/{id}")
    public String deleteSafe(@PathVariable Long id) {
        db.deletePersonSafe(id);
        return "Deleted safe";
    }

    // Уязвимая авторизация
// GET /api/persons/login-vuln?name=...&password=...
    @GetMapping("/login-vuln")
    public String loginVuln(@RequestParam String name, @RequestParam String password) {
        Person person = db.loginVulnerable(name, password);

        if (person != null) {
            return "Login success (vulnerable): " + person.getName();
        }

        return "Invalid credentials";
    }

    // Безопасная авторизация
// GET /api/persons/login-safe?name=...&password=...
    @GetMapping("/login-safe")
    public String loginSafe(@RequestParam String name, @RequestParam String password) {
        Person person = db.loginSafe(name, password);

        if (person != null) {
            return "Login success (safe): " + person.getName();
        }

        return "Invalid credentials";
    }

}