package kigali.clinic.rw.repository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // ---------- existing ----------
    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAsc(UUID doctorId, AppointmentStatus status);

    @Query("SELECT p.firstName, p.lastName, d.firstName, d.lastName, a.appointmentDate "
         + "FROM Appointment a JOIN a.patient p JOIN a.doctor d "
         + "WHERE a.status = :status AND a.appointmentDate < CURRENT_DATE")
    List<Object[]> findOverdueAppointments(@Param("status") AppointmentStatus status);

    @Query("SELECT d, COUNT(a) FROM Appointment a JOIN a.doctor d GROUP BY d ORDER BY COUNT(a) DESC")
    List<Object[]> countAppointmentsPerDoctor();

    @Query("SELECT a FROM Appointment a JOIN a.doctor d JOIN d.specializations s WHERE LOWER(s.name) = LOWER(:name)")
    List<Appointment> findByDoctorSpecialization(@Param("name") String name);

    Page<Appointment> findByDoctorId(UUID doctorId, Pageable pageable);

    // ---------- A2 DERIVED ----------
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    // ---------- A3 DERIVED ----------
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(Date start, Date end);

    // ---------- A4 DERIVED ----------
    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, Date appointmentDate, AppointmentStatus status);

    // ---------- C1 JPQL ----------
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countByStatus();

    // ---------- C4 JPQL ----------
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Appointment a SET a.status = :newStatus "
         + "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status <> :keepStatus")
    int cancelDayOfDoctor(@Param("doctorId") UUID doctorId,
                          @Param("date") Date date,
                          @Param("newStatus") AppointmentStatus newStatus,
                          @Param("keepStatus") AppointmentStatus keepStatus);

    // ---------- Bonus: JPQL DELETE ----------
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Appointment a WHERE a.status = :status AND a.appointmentDate < :date")
    int deleteByStatusBefore(@Param("status") AppointmentStatus status, @Param("date") Date date);
}