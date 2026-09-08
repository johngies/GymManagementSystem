package com.gym.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gym.dto.TrainerRequest;
import com.gym.dto.TrainerResponse;
import com.gym.entity.Trainer;
import com.gym.exception.DuplicateResourceException;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.TrainerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerService {

    private final TrainerRepository trainerRepository;

    public List<TrainerResponse> getAllTrainers() {
        return trainerRepository.findAll()
                .stream()
                .map(TrainerResponse::fromEntity)
                .toList();
    }

    public TrainerResponse getTrainerById(Long id) {
        return trainerRepository.findById(id)
                .map(TrainerResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));
    }

    public TrainerResponse getTrainerByEmail(String email) {
        return trainerRepository.findByEmail(email)
                .map(TrainerResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with email: " + email));
    }

    public Trainer getTrainerEntity(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));
    }

    public List<TrainerResponse> getTrainersBySpecialty(String specialty) {
        return trainerRepository.findBySpecialtyIgnoreCase(specialty)
                .stream()
                .map(TrainerResponse::fromEntity)
                .toList();
    }

    @Transactional
    public TrainerResponse createTrainer(TrainerRequest request) {
        if (trainerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Trainer with email " + request.email() + " already exists");
        }

        Trainer trainer = Trainer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .specialty(request.specialty())
                .build();

        Trainer savedTrainer = trainerRepository.save(trainer);
        return TrainerResponse.fromEntity(savedTrainer);
    }

    @Transactional
    public TrainerResponse updateTrainer(Long id, TrainerRequest request) {
        Trainer trainer = getTrainerEntity(id);

        if (!trainer.getEmail().equalsIgnoreCase(request.email()) && trainerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Trainer with email " + request.email() + " already exists");
        }

        trainer.setFirstName(request.firstName());
        trainer.setLastName(request.lastName());
        trainer.setEmail(request.email());
        trainer.setPhone(request.phone());
        trainer.setSpecialty(request.specialty());

        Trainer updatedTrainer = trainerRepository.save(trainer);
        return TrainerResponse.fromEntity(updatedTrainer);
    }

    @Transactional
    public void deleteTrainer(Long id) {
        if (!trainerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trainer not found with id: " + id);
        }
        trainerRepository.deleteById(id);
    }
}