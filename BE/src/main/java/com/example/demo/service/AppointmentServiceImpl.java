package com.example.demo.service;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.model.Appointment;
import com.example.demo.model.Patient;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final PatientRepository patientRepository;

    private final ModelMapper modelMapper;

    @Override
    public AppointmentDto save(AppointmentDto AppointmentDto) {
        Appointment appointment = toEntity(AppointmentDto);
        appointment = appointmentRepository.save(appointment);
        return toDto(appointment);
    }

    @Override
    public List<AppointmentDto> getAll() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream().map(this::toDto).toList();
    }

    private Appointment toEntity(AppointmentDto appointmentDto) {
        Optional<Patient> patient = patientRepository.findById(appointmentDto.getAppointmentId());
        Appointment appointment = modelMapper.map(appointmentDto, Appointment.class);
        appointment.setPatient(patient.orElse(null));
        return appointment;
    }

    private AppointmentDto toDto(Appointment appointment) {
        int patientId = appointment.getPatient().getId();
        AppointmentDto appointmentDto = modelMapper.map(appointment, AppointmentDto.class);
        appointmentDto.setPatientId(patientId);
        return appointmentDto;
    }

}
