package com.example.demo.service;

import com.example.demo.dto.PatientDto;
import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    private final ModelMapper modelMapper;

    @Override
    public PatientDto save(PatientDto patientDto) {
        Patient patient = toEntity(patientDto);
        patient = patientRepository.save(patient);
        return toDto(patient);
    }

    @Override
    public List<PatientDto> getAll() {
        List<Patient> Patient = patientRepository.findAll();
        return Patient.stream().map(this::toDto).toList();
    }

    private Patient toEntity(PatientDto patientDto) {
        Patient patient = modelMapper.map(patientDto, Patient.class);
        return patient;
    }

    private PatientDto toDto(Patient patient) {
        PatientDto patientDto = modelMapper.map(patient, PatientDto.class);
        return patientDto;
    }

    @Override
    public List<PatientDto> findAll() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(this::toDto).toList();
    }

    @Override
    public String findNameById(int id) {
        Patient patient = patientRepository.findById(id).orElse(null);
        return patient.getFullName();
    }
}
