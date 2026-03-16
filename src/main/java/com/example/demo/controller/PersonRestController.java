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
}