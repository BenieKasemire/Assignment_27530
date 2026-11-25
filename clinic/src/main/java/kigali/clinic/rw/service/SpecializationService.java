package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specRepo;

    @Autowired
    private DoctorRepository docRepo;

    public String saveSpecialization(Specialization specialization) {
        if (specRepo.findByNameIgnoreCase(specialization.getName()).isPresent()) {
            return "Specialization already exists";
        }
        specialization.setId(null);
        specRepo.save(specialization);
        return "Specialization is saved successfully";
    }

    public List<Specialization> getAllSpecializations() {
        return specRepo.findAll();
    }

    public Optional<Specialization> getSpecializationById(UUID id) {
        return specRepo.findById(id);
    }

    public String updateSpecialization(UUID id, Specialization newSpecialization) {
        Optional<Specialization> found = specRepo.findById(id);
        if (found.isEmpty()) {
            return "Specialization not found";
        }

        Optional<Specialization> sameName = specRepo.findByNameIgnoreCase(newSpecialization.getName());
        if (sameName.isPresent() && !sameName.get().getId().equals(id)) {
            return "Specialization already exists";
        }

        Specialization specialization = found.get();
        specialization.setName(newSpecialization.getName());
        specRepo.save(specialization);
        return "Specialization is updated successfully";
    }

    @Transactional
    public String deleteSpecialization(UUID id) {
        Optional<Specialization> found = specRepo.findById(id);
        if (found.isEmpty()) {
            return "Specialization not found";
        }
        if (!found.get().getDoctors().isEmpty()) {
            return "Specialization is held by a doctor, unassign it first";
        }
        specRepo.deleteById(id);
        return "Specialization is deleted successfully";
    }

    @Transactional
    public String assignDoctor(UUID specializationId, UUID doctorId) {
        Optional<Specialization> specialization = specRepo.findById(specializationId);
        if (specialization.isEmpty()) {
            return "Specialization not found";
        }
        Optional<Doctor> doctor = docRepo.findById(doctorId);
        if (doctor.isEmpty()) {
            return "Doctor not found";
        }

        Specialization spec = specialization.get();
        if (spec.getDoctors().contains(doctor.get())) {
            return "Doctor already has this specialization";
        }
        spec.getDoctors().add(doctor.get());
        specRepo.save(spec);
        return "Specialization assigned to doctor successfully";
    }

    @Transactional
    public String unassignDoctor(UUID specializationId, UUID doctorId) {
        Optional<Specialization> specialization = specRepo.findById(specializationId);
        if (specialization.isEmpty()) {
            return "Specialization not found";
        }
        Optional<Doctor> doctor = docRepo.findById(doctorId);
        if (doctor.isEmpty()) {
            return "Doctor not found";
        }

        Specialization spec = specialization.get();
        if (!spec.getDoctors().remove(doctor.get())) {
            return "Doctor does not have this specialization";
        }
        specRepo.save(spec);
        return "Specialization unassigned from doctor successfully";
    }

    // B3
    public List<Specialization> getUnusedSpecializations() {
        return specRepo.findUnusedSpecializations();
    }
}