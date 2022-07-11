package com.example.springjta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ApiRestController {

    private final JdbcTemplate a, b;

    @Autowired
    public ApiRestController(DataSource a, DataSource b) {
        this.a = new JdbcTemplate(a);
        this.b = new JdbcTemplate(b);
    }

    @GetMapping("/pets")
    public Collection<String> pets() {
        return a.query("select * from PET", (rs, rowNum) -> rs.getString("nickname"));
    }

    @GetMapping("/messages")
    public Collection<String> messages() {
        return b.query("select * from MESSAGE", (rs, rowNum) -> rs.getString("message"));
    }

    @PostMapping
    @Transactional
    public void write(@RequestBody Map<String, String> payload, @RequestParam Optional<Boolean> rollback) {

        String name = payload.get("name");
        String msg = "Hello " + name + "!";

        a.update("insert into PET (id, nickname) values (?, ?)", UUID.randomUUID(), name);
        b.update("insert into MESSAGE (id, message) values (?, ?)", UUID.randomUUID(), msg);

        if (rollback.orElse(false)) {
            throw new RuntimeException("Couldn't write the data to the database!");
        }
    }
}
