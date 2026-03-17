package com.example.demo.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private Long id;
    private String name;
    private String password;


    public Person(Long id, String name) {
        this.id = id;
        this.name = name;

    }
}

