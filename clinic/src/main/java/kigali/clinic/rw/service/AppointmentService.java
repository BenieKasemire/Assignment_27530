package kigali.clinic.rw.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository apptRepo;

    @Autowired
    private DoctorRepository docRepo;

    @Autowired
    private PatientRepository patRepo;

    // A4: no double booking
    public String saveAppointment(Appointment appointment) {
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return "Appointment must have a doctor, send the doctor id";
        }
        if (appointment.getPatient() == null || appointment.getPatient().getId() == null) {
            return "Appointment must have a patient, send the patient id";
        }

        Optional<Doctor> doctor = docRepo.findById(appointment.getDoctor().getId());
        if (doctor.isEmpty()) {
            return "Doctor not found";
        }
        Optional<Patient> patient = patRepo.findById(appointment.getPatient().getId());
        if (patient.isEmpty()) {
            return "Patient not found";
        }

        if (apptRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                doctor.get().getId(), appointment.getAppointmentDate(), AppointmentStatus.CANCELLED)) {
            return "Doctor is already booked on that date";
        }

        appointment.setId(null);
        appointment.setDoctor(doctor.get());
        appointment.setPatient(patient.get());
        apptRepo.save(appointment);
        return "Appointment is saved successfully";
    }

    public List<Appointment> getAllAppointments() {
        return apptRepo.findAll();
    }

    public List<Appointment> getAppointmentsOfDoctorByStatus(UUID doctorId, AppointmentStatus status) {
        return apptRepo.findByDoctorIdAndStatusOrderByAppointmentDateAsc(doctorId, status);
    }

    public List<Object[]> getOverdueAppointments(AppointmentStatus status) {
        return apptRepo.findOverdueAppointments(status);
    }

    public List<Object[]> getAppointmentCountPerDoctor() {
        return apptRepo.countAppointmentsPerDoctor();
    }

    public List<Appointment> getAppointmentsBySpecialization(String name) {
        return apptRepo.findByDoctorSpecialization(name);
    }

    public Page<Appointment> getAppointmentsOfDoctorPaged(UUID doctorId, Pageable pageable) {
        return apptRepo.findByDoctorId(doctorId, pageable);
    }

    // A2
    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return apptRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    // A3
    public List<Appointment> getAppointmentsBetween(LocalDate start, LocalDate end) {
        return apptRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                Date.valueOf(start), Date.valueOf(end));
    }

    // C1
    public List<Object[]> getCountByStatus() {
        return apptRepo.countByStatus();
    }

    // C4
    @Transactional
    public String cancelDay(UUID doctorId, LocalDate date) {
        int count = apptRepo.cancelDayOfDoctor(doctorId, Date.valueOf(date),
                AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
        return count + " appointments cancelled";
    }

    // Bonus: page
    public Page<Appointment> getAppointmentsPage(Pageable pageable) {
        return apptRepo.findAll(pageable);
    }

    // Bonus: delete
    @Transactional
    public String deleteCancelledBefore(LocalDate date) {
        int count = apptRepo.deleteByStatusBefore(AppointmentStatus.CANCELLED, Date.valueOf(date));
        return count + " appointments deleted";
    }
}