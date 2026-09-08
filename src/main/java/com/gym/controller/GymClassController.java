package com.gym.controller;

import com.gym.dto.GymClassRequest;
import com.gym.dto.GymClassResponse;
import com.gym.service.GymClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class GymClassController {

    private final GymClassService gymClassService;

    @GetMapping
    public ResponseEntity<List<GymClassResponse>> getAllClasses(
            @RequestParam(required = false) Long trainerId) {
        if (trainerId != null) {
            return ResponseEntity.ok(gymClassService.getClassesByTrainer(trainerId));
        }
        return ResponseEntity.ok(gymClassService.getAllClasses());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<GymClassResponse>> getUpcomingClasses() {
        return ResponseEntity.ok(gymClassService.getUpcomingClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymClassResponse> getClassById(@PathVariable Long id) {
        return ResponseEntity.ok(gymClassService.getClassById(id));
    }

    @PostMapping
    public ResponseEntity<GymClassResponse> createClass(@Valid @RequestBody GymClassRequest request) {
        GymClassResponse created = gymClassService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymClassResponse> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody GymClassRequest request) {
        return ResponseEntity.ok(gymClassService.updateClass(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        gymClassService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}
