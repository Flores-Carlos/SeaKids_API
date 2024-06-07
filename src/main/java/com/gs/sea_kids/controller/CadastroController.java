package com.gs.sea_kids.controller;

import com.gs.sea_kids.exception.ResourceNotFoundException;
import com.gs.sea_kids.model.Cadastro;
import com.gs.sea_kids.repo.CadastroRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/cadastros")
@Validated
public class CadastroController {

    @Autowired
    private CadastroRepo cadastroRepo;

    @GetMapping
    @Operation(summary = "Lista todos os cadastros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao listar os cadastros"),
            @ApiResponse(responseCode = "404", description = "Nenhum cadastro encontrado")
    })
    public ResponseEntity<CollectionModel<EntityModel<Cadastro>>> getCadastros(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cadastro> cadastrosPage = cadastroRepo.findAll(pageable);

        List<EntityModel<Cadastro>> cadastros = cadastrosPage.stream()
                .map(cadastro -> EntityModel.of(cadastro,
                        linkTo(methodOn(CadastroController.class).getCadastro(cadastro.getId())).withSelfRel(),
                        linkTo(methodOn(CadastroController.class).getCadastros(page, size)).withRel("cadastros")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Cadastro>> collectionModel = CollectionModel.of(cadastros);
        collectionModel.add(linkTo(methodOn(CadastroController.class).getCadastros(page, size)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém detalhes de um cadastro específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao obter detalhes do cadastro"),
            @ApiResponse(responseCode = "404", description = "Cadastro não encontrado")
    })
    public ResponseEntity<EntityModel<Cadastro>> getCadastro(@PathVariable Long id) {
        Cadastro cadastro = cadastroRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cadastro não encontrado pelo id :: " + id));

        EntityModel<Cadastro> cadastroModel = EntityModel.of(cadastro,
                linkTo(methodOn(CadastroController.class).getCadastro(id)).withSelfRel(),
                linkTo(methodOn(CadastroController.class).getCadastros(0, 10)).withRel("cadastros"));

        return ResponseEntity.ok(cadastroModel);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um novo cadastro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sucesso ao criar um novo cadastro"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<EntityModel<Cadastro>> saveCadastro(@Valid @RequestBody Cadastro cadastro) {
        Cadastro savedCadastro = cadastroRepo.save(cadastro);

        EntityModel<Cadastro> cadastroModel = EntityModel.of(savedCadastro,
                linkTo(methodOn(CadastroController.class).getCadastro(savedCadastro.getId())).withSelfRel(),
                linkTo(methodOn(CadastroController.class).getCadastros(0, 10)).withRel("cadastros"));

        return ResponseEntity.status(HttpStatus.CREATED).body(cadastroModel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um cadastro existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao atualizar o cadastro"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Cadastro não encontrado")
    })
    public ResponseEntity<EntityModel<Cadastro>> updateCadastro(@PathVariable Long id, @Valid @RequestBody Cadastro cadastro) {
        Cadastro existingCadastro = cadastroRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cadastro não encontrado pelo id :: " + id));

        existingCadastro.setNome(cadastro.getNome());
        existingCadastro.setEmail(cadastro.getEmail());
        existingCadastro.setSenha(cadastro.getSenha());
        Cadastro updatedCadastro = cadastroRepo.save(existingCadastro);

        EntityModel<Cadastro> cadastroModel = EntityModel.of(updatedCadastro,
                linkTo(methodOn(CadastroController.class).getCadastro(updatedCadastro.getId())).withSelfRel(),
                linkTo(methodOn(CadastroController.class).getCadastros(0, 10)).withRel("cadastros"));

        return ResponseEntity.ok(cadastroModel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um cadastro existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sucesso ao deletar o cadastro"),
            @ApiResponse(responseCode = "404", description = "Cadastro não encontrado")
    })
    public ResponseEntity<Void> deleteCadastro(@PathVariable Long id) {
        Cadastro existingCadastro = cadastroRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cadastro não encontrado pelo id :: " + id));

        cadastroRepo.delete(existingCadastro);
        return ResponseEntity.noContent().build();
    }
}
