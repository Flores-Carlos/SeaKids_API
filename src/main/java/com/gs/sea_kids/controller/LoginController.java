package com.gs.sea_kids.controller;

import com.gs.sea_kids.exception.ResourceNotFoundException;
import com.gs.sea_kids.model.Login;
import com.gs.sea_kids.repo.LoginRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/logins")
@Validated
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginRepo loginRepo;

    @GetMapping
    @Operation(summary = "Lista todos os logins")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao listar os logins"),
            @ApiResponse(responseCode = "404", description = "Nenhum login encontrado")
    })
    public ResponseEntity<CollectionModel<EntityModel<Login>>> getLogins(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Login> loginPage = loginRepo.findAll(pageable);

        List<EntityModel<Login>> logins = loginPage.stream()
                .map(login -> EntityModel.of(login,
                        linkTo(methodOn(LoginController.class).getLogin(login.getId())).withSelfRel(),
                        linkTo(methodOn(LoginController.class).getLogins(page, size)).withRel("logins")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Login>> collectionModel = CollectionModel.of(logins);
        collectionModel.add(linkTo(methodOn(LoginController.class).getLogins(page, size)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém detalhes de um login específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao obter detalhes do login"),
            @ApiResponse(responseCode = "404", description = "Login não encontrado")
    })
    public ResponseEntity<EntityModel<Login>> getLogin(@PathVariable Long id) {
        Login login = loginRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Login não encontrado pelo id :: " + id));

        EntityModel<Login> loginModel = EntityModel.of(login,
                linkTo(methodOn(LoginController.class).getLogin(id)).withSelfRel(),
                linkTo(methodOn(LoginController.class).getLogins(0, 10)).withRel("logins"));

        return ResponseEntity.ok(loginModel);
    }

    @PostMapping
    @Operation(summary = "Cria um novo login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sucesso ao criar um novo login"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<EntityModel<Login>> saveLogin(@Valid @RequestBody Login login) {

        Login savedLogin = loginRepo.save(login);

        EntityModel<Login> loginModel = EntityModel.of(savedLogin,
                linkTo(methodOn(LoginController.class).getLogin(savedLogin.getId())).withSelfRel(),
                linkTo(methodOn(LoginController.class).getLogins(0, 10)).withRel("logins"));

        return ResponseEntity.status(HttpStatus.CREATED).body(loginModel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um login existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao atualizar o login"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Login não encontrado")
    })
    public ResponseEntity<EntityModel<Login>> updateLogin(@PathVariable Long id, @Valid @RequestBody Login login) {
        Login existingLogin = loginRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Login não encontrado pelo id :: " + id));

        existingLogin.setEmail(login.getEmail());
        existingLogin.setSenha(login.getSenha());
        Login updatedLogin = loginRepo.save(existingLogin);

        EntityModel<Login> loginModel = EntityModel.of(updatedLogin,
                linkTo(methodOn(LoginController.class).getLogin(updatedLogin.getId())).withSelfRel(),
                linkTo(methodOn(LoginController.class).getLogins(0, 10)).withRel("logins"));

        return ResponseEntity.ok(loginModel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um login existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sucesso ao deletar o login"),
            @ApiResponse(responseCode = "404", description = "Login não encontrado")
    })
    public ResponseEntity<Void> deleteLogin(@PathVariable Long id) {
        Login existingLogin = loginRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Login não encontrado pelo id :: " + id));

        loginRepo.delete(existingLogin);
        return ResponseEntity.noContent().build();
    }
}
