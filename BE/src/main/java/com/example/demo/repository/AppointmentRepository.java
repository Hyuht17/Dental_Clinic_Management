package com.example.demo.repository;

import com.example.demo.dto.DentistDto;
import com.example.demo.model.Appointment;
import com.example.demo.model.Dentist;
import com.example.demo.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.FROM;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    @Query("""
        SELECT d
        FROM Dentist d
        JOIN Treatment t ON d.id = t.dentist.id
        JOIN Appointment a ON a.patient.id = t.patient.id
        Join Patient p ON p.id = t.patient.id
        WHERE a.id = :appointmentId
    """)
    Optional<Dentist> findAppointmentWithDentist(@Param("appointmentId") int appointmentId);

    @Query("SELECT a FROM Appointment a WHERE a.id = :id")
    Appointment findById(@Param("id") int id);

    @Query("""
        SELECT a
        FROM Appointment a
        JOIN Patient p ON p.id = a.patient.id
        JOIN Treatment t ON p.id = t.patient.id
        WHERE t.dentist.id = :dentistId
    """)
    List<Appointment> findAppointmentByDentistId(@Param("dentistId") int dentistId);

}

