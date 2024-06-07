package com.gs.sea_kids.controller;

import com.gs.sea_kids.exception.ResourceNotFoundException;
import com.gs.sea_kids.model.Cliente;
import com.gs.sea_kids.repo.ClienteRepo;
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
@RequestMapping("/clientes")
@Validated
public class ClienteController {

    @Autowired
    private ClienteRepo clienteRepo;

    @GetMapping
    @Operation(summary = "Lista todos os clientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao listar os clientes"),
            @ApiResponse(responseCode = "404", description = "Nenhum cliente encontrado")
    })
    public ResponseEntity<CollectionModel<EntityModel<Cliente>>> getClientes(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientePage = clienteRepo.findAll(pageable);

        List<EntityModel<Cliente>> clientes = clientePage.stream()
                .map(cliente -> EntityModel.of(cliente,
                        linkTo(methodOn(ClienteController.class).getCliente(cliente.getId())).withSelfRel(),
                        linkTo(methodOn(ClienteController.class).getClientes(page, size)).withRel("clientes")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Cliente>> collectionModel = CollectionModel.of(clientes);
        collectionModel.add(linkTo(methodOn(ClienteController.class).getClientes(page, size)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém detalhes de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao obter detalhes do cliente"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<EntityModel<Cliente>> getCliente(@PathVariable Long id) {
        Cliente cliente = clienteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado pelo id :: " + id));

        EntityModel<Cliente> clienteModel = EntityModel.of(cliente,
                linkTo(methodOn(ClienteController.class).getCliente(id)).withSelfRel(),
                linkTo(methodOn(ClienteController.class).getClientes(0, 10)).withRel("clientes"));

        return ResponseEntity.ok(clienteModel);
    }

    @PostMapping
    @Operation(summary = "Cria um novo cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sucesso ao criar um novo cliente"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<EntityModel<Cliente>> saveCliente(@Valid @RequestBody Cliente cliente) {
        Cliente savedCliente = clienteRepo.save(cliente);

        EntityModel<Cliente> clienteModel = EntityModel.of(savedCliente,
                linkTo(methodOn(ClienteController.class).getCliente(savedCliente.getId())).withSelfRel(),
                linkTo(methodOn(ClienteController.class).getClientes(0, 10)).withRel("clientes"));

        return ResponseEntity.status(HttpStatus.CREATED).body(clienteModel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao atualizar o cliente"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<EntityModel<Cliente>> updateCliente(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        Cliente existingCliente = clienteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado pelo id :: " + id));

        existingCliente.setNome(cliente.getNome());
        existingCliente.setEmail(cliente.getEmail());
        Cliente updatedCliente = clienteRepo.save(existingCliente);

        EntityModel<Cliente> clienteModel = EntityModel.of(updatedCliente,
                linkTo(methodOn(ClienteController.class).getCliente(updatedCliente.getId())).withSelfRel(),
                linkTo(methodOn(ClienteController.class).getClientes(0, 10)).withRel("clientes"));

        return ResponseEntity.ok(clienteModel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sucesso ao deletar o cliente"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        Cliente existingCliente = clienteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado pelo id :: " + id));

        clienteRepo.delete(existingCliente);
        return ResponseEntity.noContent().build();
    }
}
