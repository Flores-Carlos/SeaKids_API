package com.gs.sea_kids.controller;

import com.gs.sea_kids.exception.ResourceNotFoundException;
import com.gs.sea_kids.model.Cadastro;
import com.gs.sea_kids.repo.CadastroRepo;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/cadastros")
@Validated
public class CadastroController {

    @Autowired
    private CadastroRepo cadastroRepo;

    @GetMapping
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
    public ResponseEntity<EntityModel<Cadastro>> getCadastro(@PathVariable Long id) {
        Cadastro cadastro = cadastroRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cadastro não encontrado pelo id :: " + id));

        EntityModel<Cadastro> cadastroModel = EntityModel.of(cadastro,
                linkTo(methodOn(CadastroController.class).getCadastro(id)).withSelfRel(),
                linkTo(methodOn(CadastroController.class).getCadastros(0, 10)).withRel("cadastros"));

        return ResponseEntity.ok(cadastroModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Cadastro>> saveCadastro(@Valid @RequestBody Cadastro cadastro) {
        Cadastro savedCadastro = cadastroRepo.save(cadastro);

        EntityModel<Cadastro> cadastroModel = EntityModel.of(savedCadastro,
                linkTo(methodOn(CadastroController.class).getCadastro(savedCadastro.getId())).withSelfRel(),
                linkTo(methodOn(CadastroController.class).getCadastros(0, 10)).withRel("cadastros"));

        return ResponseEntity.status(HttpStatus.CREATED).body(cadastroModel);
    }

    @PutMapping("/{id}")
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
    public ResponseEntity<Void> deleteCadastro(@PathVariable Long id) {
        Cadastro existingCadastro = cadastroRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cadastro não encontrado pelo id :: " + id));

        cadastroRepo.delete(existingCadastro);
        return ResponseEntity.noContent().build();
    }
}
