package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloController {

    // 1. Метод для обычного захода на страницу (GET)
    @GetMapping("/hello")
    public String showPage(Model model) {
        // При первом открытии страницы выводим базовое сообщение
        String name = "";

        model.addAttribute("message", name);
        return "hello";
    }

    // 2. Метод для обработки формы (POST)
    @PostMapping("/hello")
    public String addName(
            // @RequestParam берет текст из <input name="userName">
            @RequestParam("userName") String name,
            Model model
    ) {
        // Создаем новое сообщение с именем, которое ввел пользователь
        String resultMessage = name + " !";

        // Передаем его обратно в HTML
        model.addAttribute("message", resultMessage);

        // Снова возвращаем шаблон hello.html, но уже с новыми данными
        return "hello";
    }
}
