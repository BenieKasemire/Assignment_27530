package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patRepo;

    @Autowired
    private DoctorRepository docRepo;

    public String savePatient(Patient patient) {
        patient.setId(null);
        patRepo.save(patient);
        return "Patient is saved successfully";
    }

    public List<Patient> getAllPatients() {
        return patRepo.findAll();
    }

    public Optional<Patient> getPatientById(UUID id) {
        return patRepo.findById(id);
    }

    public String updatePatient(UUID id, Patient newPatient) {
        Optional<Patient> found = patRepo.findById(id);
        if (found.isEmpty()) {
            return "Patient not found";
        }

        Patient patient = found.get();
        patient.setFirstName(newPatient.getFirstName());
        patient.setLastName(newPatient.getLastName());
        patient.setDateOfBirth(newPatient.getDateOfBirth());
        patRepo.save(patient);
        return "Patient is updated successfully";
    }

    @Transactional
    public String deletePatient(UUID id) {
        Optional<Patient> found = patRepo.findById(id);
        if (found.isEmpty()) {
            return "Patient not found";
        }
        if (!found.get().getAppointments().isEmpty()) {
            return "Patient has appointments, cannot be deleted";
        }
        patRepo.deleteById(id);
        return "Patient is deleted successfully";
    }

    // A1
    public List<Patient> getPatientsByLastName(String lastName) {
        return patRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    // B4 (empty Optional = doctor does not exist)
    public Optional<List<Patient>> getPatientsOfDoctor(UUID doctorId) {
        if (!docRepo.existsById(doctorId)) {
            return Optional.empty();
        }
        return Optional.of(patRepo.findPatientsOfDoctor(doctorId));
    }

    // C2
    public List<Patient> getFrequentPatients(long min) {
        return patRepo.findFrequentPatients(min);
    }
}