package com.gs.sea_kids.controller;

import com.gs.sea_kids.exception.ResourceNotFoundException;
import com.gs.sea_kids.model.Video;
import com.gs.sea_kids.repo.VideoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/videos")
@Validated
public class VideoController {

    @Autowired
    private VideoRepo videoRepo;

    @GetMapping
    public ResponseEntity<List<Video>> getVideos() {
        List<Video> videos = videoRepo.findAll();
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideo(@PathVariable Long id) {
        Video video = videoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado pelo id :: " + id));
        return ResponseEntity.ok(video);
    }

    @PostMapping
    public ResponseEntity<Void> saveVideo(@Valid @RequestBody Video video) {
        videoRepo.save(video);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateVideo(@PathVariable Long id, @Valid @RequestBody Video video) {
        Video existingVideo = videoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado pelo id :: " + id));
        existingVideo.setTitulo(video.getTitulo());
        existingVideo.setLink(video.getLink());
        videoRepo.save(existingVideo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        Video existingVideo = videoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado pelo id :: " + id));
        videoRepo.delete(existingVideo);
        return ResponseEntity.noContent().build();
    }
}
