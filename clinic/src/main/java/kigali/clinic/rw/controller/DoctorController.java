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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;

@RestController
@RequestMapping(value = {"/api/doctor", "/api/doctors"})
public class DoctorController {

    @Autowired
    private DoctorService docServe;

    @PostMapping(value = "/save")
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        String message = docServe.saveDoctor(doctor);
        return new ResponseEntity<>(message, statusOf(message));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllDoctors() {
        return new ResponseEntity<>(docServe.getAllDoctors(), HttpStatus.OK);
    }

    // B1
    @GetMapping(value = "/by-specialization")
    public ResponseEntity<?> getBySpecialization(@RequestParam String name) {
        return new ResponseEntity<>(docServe.getDoctorsBySpecialization(name), HttpStatus.OK);
    }

    // B2
    @GetMapping(value = "/without-office")
    public ResponseEntity<?> getWithoutOffice() {
        return new ResponseEntity<>(docServe.getDoctorsWithoutOffice(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable("id") UUID id) {
        Optional<Doctor> doctor = docServe.getDoctorById(id);
        if (doctor.isPresent()) {
            return new ResponseEntity<>(doctor.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable("id") UUID id, @RequestBody Doctor doctor) {
        String message = docServe.updateDoctor(id, doctor);
        return new ResponseEntity<>(message, statusOf(message));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable("id") UUID id) {
        String message = docServe.deleteDoctor(id);
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