package com.gym.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gym.dto.GymClassRequest;
import com.gym.dto.GymClassResponse;
import com.gym.entity.GymClass;
import com.gym.entity.Trainer;
import com.gym.exception.InvalidOperationException;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.GymClassRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GymClassService {

    private final GymClassRepository gymClassRepository;
    private final TrainerService trainerService;

    public List<GymClassResponse> getAllClasses() {
        return gymClassRepository.findAll()
                .stream()
                .map(GymClassResponse::fromEntity)
                .toList();
    }

    public GymClassResponse getClassById(Long id) {
        return gymClassRepository.findById(id)
                .map(GymClassResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Gym class not found with id: " + id));
    }

    public GymClass getGymClassEntity(Long id) {
        return gymClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gym class not found with id: " + id));
    }

    public List<GymClassResponse> getClassesByTrainer(Long trainerId) {
        return gymClassRepository.findByTrainerId(trainerId)
                .stream()
                .map(GymClassResponse::fromEntity)
                .toList();
    }

    public List<GymClassResponse> getUpcomingClasses() {
        return gymClassRepository.findByStartTimeAfter(LocalDateTime.now())
                .stream()
                .map(GymClassResponse::fromEntity)
                .toList();
    }

    @Transactional
    public GymClassResponse createClass(GymClassRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new InvalidOperationException("Class end time must be after start time");
        }

        Trainer trainer = trainerService.getTrainerEntity(request.trainerId());

        GymClass gymClass = GymClass.builder()
                .name(request.name())
                .trainer(trainer)
                .capacity(request.capacity())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        GymClass saved = gymClassRepository.save(gymClass);
        return GymClassResponse.fromEntity(saved);
    }

    @Transactional
    public GymClassResponse updateClass(Long id, GymClassRequest request) {
        GymClass gymClass = getGymClassEntity(id);

        if (!request.endTime().isAfter(request.startTime())) {
            throw new InvalidOperationException("Class end time must be after start time");
        }

        if (!gymClass.getTrainer().getId().equals(request.trainerId())) {
            Trainer newTrainer = trainerService.getTrainerEntity(request.trainerId());
            gymClass.setTrainer(newTrainer);
        }

        gymClass.setName(request.name());
        gymClass.setCapacity(request.capacity());
        gymClass.setStartTime(request.startTime());
        gymClass.setEndTime(request.endTime());

        GymClass updated = gymClassRepository.save(gymClass);
        return GymClassResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteClass(Long id) {
        if (!gymClassRepository.existsById(id)) {
            throw new ResourceNotFoundException("Gym class not found with id: " + id);
        }
        gymClassRepository.deleteById(id);
    }
}
