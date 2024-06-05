package com.gs.sea_kids.controller;

import com.gs.sea_kids.exception.ResourceNotFoundException;
import com.gs.sea_kids.model.Login;
import com.gs.sea_kids.repo.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/logins")
@Validated
public class LoginController {

    @Autowired
    private LoginRepo loginRepo;

    @GetMapping
    public ResponseEntity<List<Login>> getLogins() {
        List<Login> logins = loginRepo.findAll();
        return ResponseEntity.ok(logins);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Login> getLogin(@PathVariable Long id) {
        Login login = loginRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Login não encontrado pelo id :: " + id));
        return ResponseEntity.ok(login);
    }

    @PostMapping
    public ResponseEntity<Void> saveLogin(@Valid @RequestBody Login login) {
        loginRepo.save(login);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLogin(@PathVariable Long id, @Valid @RequestBody Login login) {
        Login existingLogin = loginRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Login não encontrado pelo id :: " + id));
        existingLogin.setEmail(login.getEmail());
        existingLogin.setSenha(login.getSenha());
        loginRepo.save(existingLogin);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogin(@PathVariable Long id) {
        Login existingLogin = loginRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Login não encontrado pelo id :: " + id));
        loginRepo.delete(existingLogin);
        return ResponseEntity.noContent().build();
    }
}
