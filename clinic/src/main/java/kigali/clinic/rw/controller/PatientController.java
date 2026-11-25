package kigali.clinic.rw.controller;

import java.util.List;
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

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.PatientService;

@RestController
@RequestMapping(value = {"/api/patient", "/api/patients"})
public class PatientController {

    @Autowired
    private PatientService patServe;

    @PostMapping(value = "/save")
    public ResponseEntity<?> savePatient(@RequestBody Patient patient) {
        String message = patServe.savePatient(patient);
        return new ResponseEntity<>(message, statusOf(message));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllPatients() {
        return new ResponseEntity<>(patServe.getAllPatients(), HttpStatus.OK);
    }

    // A1
    @GetMapping(value = "/by-last-name")
    public ResponseEntity<?> getByLastName(@RequestParam String lastName) {
        return new ResponseEntity<>(patServe.getPatientsByLastName(lastName), HttpStatus.OK);
    }

    // B4
    @GetMapping(value = "/of-doctor/{doctorId}")
    public ResponseEntity<?> getPatientsOfDoctor(@PathVariable UUID doctorId) {
        Optional<List<Patient>> patients = patServe.getPatientsOfDoctor(doctorId);
        if (patients.isPresent()) {
            return new ResponseEntity<>(patients.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
    }

    // C2
    @GetMapping(value = "/frequent")
    public ResponseEntity<?> getFrequent(@RequestParam long min) {
        return new ResponseEntity<>(patServe.getFrequentPatients(min), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable("id") UUID id) {
        Optional<Patient> patient = patServe.getPatientById(id);
        if (patient.isPresent()) {
            return new ResponseEntity<>(patient.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable("id") UUID id, @RequestBody Patient patient) {
        String message = patServe.updatePatient(id, patient);
        return new ResponseEntity<>(message, statusOf(message));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable("id") UUID id) {
        String message = patServe.deletePatient(id);
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