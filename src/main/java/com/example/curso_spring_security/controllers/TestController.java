package com.example.curso_spring_security.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/publico")
    public String publico() {
        return "Este endpoint é público";
    }

    @GetMapping("/privado")
    public String privado() {
        return "Você acessou o endpoint privado";
    }

    @GetMapping("/sobre")
    public String sobre() {
        return "Sobre a aplicação";
    }


}
