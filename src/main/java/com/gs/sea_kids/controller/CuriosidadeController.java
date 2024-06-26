package com.gs.sea_kids.controller;

import com.gs.sea_kids.exception.ResourceNotFoundException;
import com.gs.sea_kids.model.Curiosidade;
import com.gs.sea_kids.repo.CuriosidadeRepo;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/curiosidades")
@Validated
public class CuriosidadeController {

    @Autowired
    private CuriosidadeRepo curiosidadeRepo;

    @GetMapping
    @Operation(summary = "Lista todas as curiosidades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao listar as curiosidades"),
            @ApiResponse(responseCode = "404", description = "Nenhuma curiosidade encontrada")
    })
    public ResponseEntity<CollectionModel<EntityModel<Curiosidade>>> getCuriosidades(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Curiosidade> curiosidadePage = curiosidadeRepo.findAll(pageable);

        List<EntityModel<Curiosidade>> curiosidades = curiosidadePage.stream()
                .map(curiosidade -> EntityModel.of(curiosidade,
                        linkTo(methodOn(CuriosidadeController.class).getCuriosidade(curiosidade.getId())).withSelfRel(),
                        linkTo(methodOn(CuriosidadeController.class).getCuriosidades(page, size)).withRel("curiosidades")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Curiosidade>> collectionModel = CollectionModel.of(curiosidades);
        collectionModel.add(linkTo(methodOn(CuriosidadeController.class).getCuriosidades(page, size)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém detalhes de uma curiosidade específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao obter detalhes da curiosidade"),
            @ApiResponse(responseCode = "404", description = "Curiosidade não encontrada")
    })
    public ResponseEntity<EntityModel<Curiosidade>> getCuriosidade(@PathVariable Long id) {
        Curiosidade curiosidade = curiosidadeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curiosidade não encontrada pelo id :: " + id));

        EntityModel<Curiosidade> curiosidadeModel = EntityModel.of(curiosidade,
                linkTo(methodOn(CuriosidadeController.class).getCuriosidade(id)).withSelfRel(),
                linkTo(methodOn(CuriosidadeController.class).getCuriosidades(0, 10)).withRel("curiosidades"));

        return ResponseEntity.ok(curiosidadeModel);
    }

    @PostMapping
    @Operation(summary = "Cria uma nova curiosidade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sucesso ao criar uma nova curiosidade"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<EntityModel<Curiosidade>> saveCuriosidade(@Valid @RequestBody Curiosidade curiosidade) {
        Curiosidade savedCuriosidade = curiosidadeRepo.save(curiosidade);

        EntityModel<Curiosidade> curiosidadeModel = EntityModel.of(savedCuriosidade,
                linkTo(methodOn(CuriosidadeController.class).getCuriosidade(savedCuriosidade.getId())).withSelfRel(),
                linkTo(methodOn(CuriosidadeController.class).getCuriosidades(0, 10)).withRel("curiosidades"));

        return ResponseEntity.status(HttpStatus.CREATED).body(curiosidadeModel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma curiosidade existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao atualizar a curiosidade"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Curiosidade não encontrada")
    })
    public ResponseEntity<EntityModel<Curiosidade>> updateCuriosidade(@PathVariable Long id, @Valid @RequestBody Curiosidade curiosidade) {
        Curiosidade existingCuriosidade = curiosidadeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curiosidade não encontrada pelo id :: " + id));

        existingCuriosidade.setTitulo(curiosidade.getTitulo());
        existingCuriosidade.setImagem(curiosidade.getImagem());
        existingCuriosidade.setTexto(curiosidade.getTexto());
        Curiosidade updatedCuriosidade = curiosidadeRepo.save(existingCuriosidade);

        EntityModel<Curiosidade> curiosidadeModel = EntityModel.of(updatedCuriosidade,
                linkTo(methodOn(CuriosidadeController.class).getCuriosidade(updatedCuriosidade.getId())).withSelfRel(),
                linkTo(methodOn(CuriosidadeController.class).getCuriosidades(0, 10)).withRel("curiosidades"));

        return ResponseEntity.ok(curiosidadeModel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma curiosidade existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sucesso ao deletar a curiosidade"),
            @ApiResponse(responseCode = "404", description = "Curiosidade não encontrada")
    })
    public ResponseEntity<Void> deleteCuriosidade(@PathVariable Long id) {
        Curiosidade existingCuriosidade = curiosidadeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curiosidade não encontrada pelo id :: " + id));

        curiosidadeRepo.delete(existingCuriosidade);
        return ResponseEntity.noContent().build();
    }
}
