package com.example.demo.service;

import com.example.demo.dto.TreatmentDto;

import java.util.List;

public interface TreatmentService {
    List<TreatmentDto> findTreatmentByDentistId(int dentistId);
}
