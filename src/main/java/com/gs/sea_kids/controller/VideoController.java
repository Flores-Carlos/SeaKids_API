package com.gs.sea_kids.controller;

import com.gs.sea_kids.exception.ResourceNotFoundException;
import com.gs.sea_kids.model.Video;
import com.gs.sea_kids.repo.VideoRepo;
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
@RequestMapping("/videos")
@Validated
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private VideoRepo videoRepo;

    @GetMapping
    @Operation(summary = "Lista todos os vídeos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao listar os vídeos"),
            @ApiResponse(responseCode = "404", description = "Nenhum vídeo encontrado")
    })
    public ResponseEntity<CollectionModel<EntityModel<Video>>> getVideos(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videoPage = videoRepo.findAll(pageable);

        List<EntityModel<Video>> videos = videoPage.stream()
                .map(video -> EntityModel.of(video,
                        linkTo(methodOn(VideoController.class).getVideo(video.getId())).withSelfRel(),
                        linkTo(methodOn(VideoController.class).getVideos(page, size)).withRel("videos")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Video>> collectionModel = CollectionModel.of(videos);
        collectionModel.add(linkTo(methodOn(VideoController.class).getVideos(page, size)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém detalhes de um vídeo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao obter detalhes do vídeo"),
            @ApiResponse(responseCode = "404", description = "Vídeo não encontrado")
    })
    public ResponseEntity<EntityModel<Video>> getVideo(@PathVariable Long id) {
        Video video = videoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado pelo id :: " + id));

        EntityModel<Video> videoModel = EntityModel.of(video,
                linkTo(methodOn(VideoController.class).getVideo(id)).withSelfRel(),
                linkTo(methodOn(VideoController.class).getVideos(0, 10)).withRel("videos"));

        return ResponseEntity.ok(videoModel);
    }

    @PostMapping
    @Operation(summary = "Cria um novo vídeo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sucesso ao criar um novo vídeo"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<EntityModel<Video>> saveVideo(@Valid @RequestBody Video video) {

        Video savedVideo = videoRepo.save(video);

        EntityModel<Video> videoModel = EntityModel.of(savedVideo,
                linkTo(methodOn(VideoController.class).getVideo(savedVideo.getId())).withSelfRel(),
                linkTo(methodOn(VideoController.class).getVideos(0, 10)).withRel("videos"));

        return ResponseEntity.status(HttpStatus.CREATED).body(videoModel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um vídeo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao atualizar o vídeo"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Vídeo não encontrado")
    })
    public ResponseEntity<EntityModel<Video>> updateVideo(@PathVariable Long id, @Valid @RequestBody Video video) {
        Video existingVideo = videoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado pelo id :: " + id));

        existingVideo.setTitulo(video.getTitulo());
        existingVideo.setLink(video.getLink());
        Video updatedVideo = videoRepo.save(existingVideo);

        EntityModel<Video> videoModel = EntityModel.of(updatedVideo,
                linkTo(methodOn(VideoController.class).getVideo(updatedVideo.getId())).withSelfRel(),
                linkTo(methodOn(VideoController.class).getVideos(0, 10)).withRel("videos"));

        return ResponseEntity.ok(videoModel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um vídeo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sucesso ao deletar o vídeo"),
            @ApiResponse(responseCode = "404", description = "Vídeo não encontrado")
    })
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        Video existingVideo = videoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado pelo id :: " + id));

        videoRepo.delete(existingVideo);
        return ResponseEntity.noContent().build();
    }
}
