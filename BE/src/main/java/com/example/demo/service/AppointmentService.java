package com.example.demo.service;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.dto.DentistDto;
import com.example.demo.model.Appointment;

import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    AppointmentDto save(AppointmentDto appointment);
    List<AppointmentDto> getAll();
    List<Appointment> findAll();
    DentistDto findDentistByAppointmentId(int appointmentId);
    List<AppointmentDto> findAppointmentsWithDentists();
    AppointmentDto findAppointmentById(int appointmentId);
    List<AppointmentDto> findAppointmentByDentistId(int dentistId);
}
