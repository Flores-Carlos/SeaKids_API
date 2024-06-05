package com.gs.sea_kids.controller;

import com.gs.sea_kids.exception.ResourceNotFoundException;
import com.gs.sea_kids.model.App;
import com.gs.sea_kids.repo.AppRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
@RequestMapping("/apps")
@Validated
public class AppController {

    @Autowired
    private AppRepo appRepo;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<App>>> getApps(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<App> appsPage = appRepo.findAll(pageable);

        List<EntityModel<App>> apps = appsPage.stream()
                .map(app -> EntityModel.of(app,
                        linkTo(methodOn(AppController.class).getApp(app.getId())).withSelfRel(),
                        linkTo(methodOn(AppController.class).getApps(page, size)).withRel("apps")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<App>> collectionModel = CollectionModel.of(apps);
        collectionModel.add(linkTo(methodOn(AppController.class).getApps(page, size)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<App>> getApp(@PathVariable Long id) {
        App app = appRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("App não encontrado pelo id :: " + id));

        EntityModel<App> appModel = EntityModel.of(app,
                linkTo(methodOn(AppController.class).getApp(id)).withSelfRel(),
                linkTo(methodOn(AppController.class).getApps(0, 10)).withRel("apps"));

        return ResponseEntity.ok(appModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<App>> saveApp(@Valid @RequestBody App app) {
        App savedApp = appRepo.save(app);

        EntityModel<App> appModel = EntityModel.of(savedApp,
                linkTo(methodOn(AppController.class).getApp(savedApp.getId())).withSelfRel(),
                linkTo(methodOn(AppController.class).getApps(0, 10)).withRel("apps"));

        return ResponseEntity.status(HttpStatus.CREATED).body(appModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<App>> updateApp(@PathVariable Long id, @Valid @RequestBody App app) {
        App existingApp = appRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("App não encontrado pelo id :: " + id));

        existingApp.setNome(app.getNome());
        existingApp.setVersao(app.getVersao());
        App updatedApp = appRepo.save(existingApp);

        EntityModel<App> appModel = EntityModel.of(updatedApp,
                linkTo(methodOn(AppController.class).getApp(updatedApp.getId())).withSelfRel(),
                linkTo(methodOn(AppController.class).getApps(0, 10)).withRel("apps"));

        return ResponseEntity.ok(appModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApp(@PathVariable Long id) {
        App existingApp = appRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("App não encontrado pelo id :: " + id));

        appRepo.delete(existingApp);
        return ResponseEntity.noContent().build();
    }
}
