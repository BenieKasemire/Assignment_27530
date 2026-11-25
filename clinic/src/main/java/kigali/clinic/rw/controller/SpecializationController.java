package kigali.clinic.rw.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.service.SpecializationService;

@RestController
@RequestMapping(value = {"/api/specialization", "/api/specializations"})
public class SpecializationController {

    @Autowired
    private SpecializationService specServe;

    @PostMapping(value = "/save")
    public ResponseEntity<?> saveSpecialization(@RequestBody Specialization specialization) {
        String message = specServe.saveSpecialization(specialization);
        return new ResponseEntity<>(message, statusOf(message));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllSpecializations() {
        return new ResponseEntity<>(specServe.getAllSpecializations(), HttpStatus.OK);
    }

    // B3
    @GetMapping(value = "/unused")
    public ResponseEntity<?> getUnused() {
        return new ResponseEntity<>(specServe.getUnusedSpecializations(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getSpecializationById(@PathVariable("id") UUID id) {
        Optional<Specialization> specialization = specServe.getSpecializationById(id);
        if (specialization.isPresent()) {
            return new ResponseEntity<>(specialization.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Specialization not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateSpecialization(@PathVariable("id") UUID id, @RequestBody Specialization specialization) {
        String message = specServe.updateSpecialization(id, specialization);
        return new ResponseEntity<>(message, statusOf(message));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteSpecialization(@PathVariable("id") UUID id) {
        String message = specServe.deleteSpecialization(id);
        return new ResponseEntity<>(message, statusOf(message));
    }

    @PostMapping(value = "/{specializationId}/assign/{doctorId}")
    public ResponseEntity<?> assignDoctor(@PathVariable UUID specializationId, @PathVariable UUID doctorId) {
        String message = specServe.assignDoctor(specializationId, doctorId);
        return new ResponseEntity<>(message, statusOf(message));
    }

    @DeleteMapping(value = "/{specializationId}/unassign/{doctorId}")
    public ResponseEntity<?> unassignDoctor(@PathVariable UUID specializationId, @PathVariable UUID doctorId) {
        String message = specServe.unassignDoctor(specializationId, doctorId);
        return new ResponseEntity<>(message, statusOf(message));
    }

    private HttpStatus statusOf(String message) {
        if (message.endsWith("successfully")) {
            return HttpStatus.OK;
        }
        if (message.endsWith("not found")) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.CONFLICT;
    }
}