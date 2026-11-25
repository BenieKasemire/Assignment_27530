package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.service.AppointmentService;

@RestController
@RequestMapping(value = {"/api/appointment", "/api/appointments"})
public class AppointmentController {

    @Autowired
    private AppointmentService apptServe;

    // A4 (existing endpoint; the service now checks for double booking)
    @PostMapping(value = "/save")
    public ResponseEntity<?> saveAppointment(@RequestBody Appointment appointment) {
        String message = apptServe.saveAppointment(appointment);
        return new ResponseEntity<>(message, statusOf(message));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllAppointments() {
        return new ResponseEntity<>(apptServe.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping(value = "/doctor/{doctorId}/status/{status}")
    public ResponseEntity<?> getByDoctorAndStatus(@PathVariable UUID doctorId, @PathVariable AppointmentStatus status) {
        return new ResponseEntity<>(apptServe.getAppointmentsOfDoctorByStatus(doctorId, status), HttpStatus.OK);
    }

    @GetMapping(value = "/overdue")
    public ResponseEntity<?> getOverdue(@RequestParam(defaultValue = "SCHEDULED") AppointmentStatus status) {
        return new ResponseEntity<>(apptServe.getOverdueAppointments(status), HttpStatus.OK);
    }

    @GetMapping(value = "/stats/per-doctor")
    public ResponseEntity<?> getCountPerDoctor() {
        return new ResponseEntity<>(apptServe.getAppointmentCountPerDoctor(), HttpStatus.OK);
    }

    @GetMapping(value = "/by-specialization")
    public ResponseEntity<?> getBySpecialization(@RequestParam String name) {
        return new ResponseEntity<>(apptServe.getAppointmentsBySpecialization(name), HttpStatus.OK);
    }

    @GetMapping(value = "/doctor/{doctorId}/page")
    public ResponseEntity<?> getDoctorAppointmentsPaged(@PathVariable UUID doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").ascending());
        return new ResponseEntity<>(apptServe.getAppointmentsOfDoctorPaged(doctorId, pageable), HttpStatus.OK);
    }

    // A2
    @GetMapping(value = "/by-status")
    public ResponseEntity<?> getByStatus(@RequestParam AppointmentStatus status) {
        return new ResponseEntity<>(apptServe.getAppointmentsByStatus(status), HttpStatus.OK);
    }

    // A3
    @GetMapping(value = "/between")
    public ResponseEntity<?> getBetween(@RequestParam String start, @RequestParam String end) {
        LocalDate from = LocalDate.parse(start);
        LocalDate to = LocalDate.parse(end);
        return new ResponseEntity<>(apptServe.getAppointmentsBetween(from, to), HttpStatus.OK);
    }

    // C1
    @GetMapping(value = "/stats/by-status")
    public ResponseEntity<?> getStatsByStatus() {
        return new ResponseEntity<>(apptServe.getCountByStatus(), HttpStatus.OK);
    }

    // C4
    @PatchMapping(value = "/cancel-day")
    public ResponseEntity<?> cancelDay(@RequestParam UUID doctorId, @RequestParam String date) {
        return new ResponseEntity<>(apptServe.cancelDay(doctorId, LocalDate.parse(date)), HttpStatus.OK);
    }

    // Bonus: pagination
    @GetMapping(value = "/page")
    public ResponseEntity<?> getPage(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     @RequestParam(defaultValue = "appointmentDate,desc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, parts[0]));
        return new ResponseEntity<>(apptServe.getAppointmentsPage(pageable), HttpStatus.OK);
    }

    // Bonus: delete
    @DeleteMapping(value = "/cancelled-before")
    public ResponseEntity<?> deleteCancelledBefore(@RequestParam String date) {
        return new ResponseEntity<>(apptServe.deleteCancelledBefore(LocalDate.parse(date)), HttpStatus.OK);
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