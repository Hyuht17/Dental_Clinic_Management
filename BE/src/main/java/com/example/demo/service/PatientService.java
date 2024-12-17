package com.example.demo.service;

import com.example.demo.dto.PatientDto;
import java.util.List;
public interface PatientService {
    PatientDto save(PatientDto patientDto);
    List<PatientDto> getAll();
    List<PatientDto> findAll();
    String findNameById(int id);
}
