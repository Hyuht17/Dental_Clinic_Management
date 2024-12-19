package com.example.demo.service;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.dto.DentistDto;
import com.example.demo.dto.TreatmentDto;
import com.example.demo.model.Appointment;
import com.example.demo.model.Dentist;
import com.example.demo.model.Patient;
import com.example.demo.model.Treatment;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
        Optional<Patient> patient = patientRepository.findById(appointmentDto.getPatientId());
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

    @Override
    public List<Appointment> findAll() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments;
    }

    @Override
    public DentistDto findDentistByAppointmentId(int appointmentId) {
        // Find Dentist associated with the Appointment by ID
        Optional<Dentist> dentistOptional = appointmentRepository.findAppointmentWithDentist(appointmentId);
        return dentistOptional.map(d -> new DentistDto(d.getId(), d.getName(), d.getImgUrl()))
                .orElse(null); // Return null if no Dentist found
    }

    @Override
    public List<AppointmentDto> findAppointmentsWithDentists() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream().map(appointment -> {
            AppointmentDto appointmentDto = toDto(appointment);
            DentistDto dentistDto = findDentistByAppointmentId(appointment.getId());
            appointmentDto.setDentist(dentistDto);  // Set DentistDto in AppointmentDto
            return appointmentDto;
        }).collect(Collectors.toList());
    }

    @Override
    public AppointmentDto findAppointmentById(int appointmentId) {
        return toDto(appointmentRepository.findById(appointmentId));
    }

    @Override
    public List<AppointmentDto> findAppointmentByDentistId(int dentistId) {
        List<Appointment> appointments = appointmentRepository.findAppointmentByDentistId(dentistId);
        return appointments.stream().map(this::toDto).toList();
    }


}
