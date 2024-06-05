package com.gs.sea_kids.controller;

import com.gs.sea_kids.exception.ResourceNotFoundException;
import com.gs.sea_kids.model.Cliente;
import com.gs.sea_kids.repo.ClienteRepo;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/clientes")
@Validated
public class ClienteController {

    @Autowired
    private ClienteRepo clienteRepo;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Cliente>>> getClientes() {
        List<EntityModel<Cliente>> clientes = clienteRepo.findAll().stream()
                .map(cliente -> EntityModel.of(cliente,
                        linkTo(methodOn(ClienteController.class).getCliente(cliente.getId())).withSelfRel(),
                        linkTo(methodOn(ClienteController.class).getClientes()).withRel("clientes")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Cliente>> collectionModel = CollectionModel.of(clientes);
        collectionModel.add(linkTo(methodOn(ClienteController.class).getClientes()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> getCliente(@PathVariable Long id) {
        Cliente cliente = clienteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado pelo id :: " + id));

        EntityModel<Cliente> clienteModel = EntityModel.of(cliente,
                linkTo(methodOn(ClienteController.class).getCliente(id)).withSelfRel(),
                linkTo(methodOn(ClienteController.class).getClientes()).withRel("clientes"));

        return ResponseEntity.ok(clienteModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Cliente>> saveCliente(@Valid @RequestBody Cliente cliente) {
        Cliente savedCliente = clienteRepo.save(cliente);

        EntityModel<Cliente> clienteModel = EntityModel.of(savedCliente,
                linkTo(methodOn(ClienteController.class).getCliente(savedCliente.getId())).withSelfRel(),
                linkTo(methodOn(ClienteController.class).getClientes()).withRel("clientes"));

        return ResponseEntity.status(HttpStatus.CREATED).body(clienteModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> updateCliente(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        Cliente existingCliente = clienteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado pelo id :: " + id));

        existingCliente.setNome(cliente.getNome());
        existingCliente.setEmail(cliente.getEmail());
        Cliente updatedCliente = clienteRepo.save(existingCliente);

        EntityModel<Cliente> clienteModel = EntityModel.of(updatedCliente,
                linkTo(methodOn(ClienteController.class).getCliente(updatedCliente.getId())).withSelfRel(),
                linkTo(methodOn(ClienteController.class).getClientes()).withRel("clientes"));

        return ResponseEntity.ok(clienteModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        Cliente existingCliente = clienteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado pelo id :: " + id));

        clienteRepo.delete(existingCliente);
        return ResponseEntity.noContent().build();
    }
}
