package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository docRepo;

    @Autowired
    private OfficeRepository offRepo;

    public String saveDoctor(Doctor doctor) {
        if (doctor.getOffice() != null && doctor.getOffice().getId() != null) {
            Optional<Office> office = offRepo.findById(doctor.getOffice().getId());
            if (office.isEmpty()) {
                return "Office not found";
            }
            if (office.get().getDoctor() != null) {
                return "Office is already assigned to another doctor";
            }
            doctor.setOffice(office.get());
        } else {
            doctor.setOffice(null); // a doctor may have no office
        }
        doctor.setId(null);
        docRepo.save(doctor);
        return "Doctor is saved successfully";
    }

    public List<Doctor> getAllDoctors() {
        return docRepo.findAll();
    }

    public Optional<Doctor> getDoctorById(UUID id) {
        return docRepo.findById(id);
    }

    public String updateDoctor(UUID id, Doctor newDoctor) {
        Optional<Doctor> found = docRepo.findById(id);
        if (found.isEmpty()) {
            return "Doctor not found";
        }

        Office newOffice = null;
        if (newDoctor.getOffice() != null && newDoctor.getOffice().getId() != null) {
            Optional<Office> office = offRepo.findById(newDoctor.getOffice().getId());
            if (office.isEmpty()) {
                return "Office not found";
            }
            Doctor owner = office.get().getDoctor();
            if (owner != null && !owner.getId().equals(id)) {
                return "Office is already assigned to another doctor";
            }
            newOffice = office.get();
        }

        Doctor doctor = found.get();
        doctor.setFirstName(newDoctor.getFirstName());
        doctor.setLastName(newDoctor.getLastName());
        doctor.setDateOfBirth(newDoctor.getDateOfBirth());
        if (newOffice != null) {
            doctor.setOffice(newOffice);
        }
        docRepo.save(doctor);
        return "Doctor is updated successfully";
    }

    @Transactional
    public String deleteDoctor(UUID id) {
        Optional<Doctor> found = docRepo.findById(id);
        if (found.isEmpty()) {
            return "Doctor not found";
        }
        Doctor doctor = found.get();
        if (!doctor.getAppointments().isEmpty()) {
            return "Doctor has appointments, cannot be deleted";
        }
        if (!doctor.getSpecializations().isEmpty()) {
            return "Doctor has specializations, cannot be deleted";
        }
        docRepo.deleteById(id);
        return "Doctor is deleted successfully";
    }

    // B1
    public List<Doctor> getDoctorsBySpecialization(String name) {
        return docRepo.findBySpecializationName(name);
    }

    // B2
    public List<Doctor> getDoctorsWithoutOffice() {
        return docRepo.findDoctorsWithoutOffice();
    }
}