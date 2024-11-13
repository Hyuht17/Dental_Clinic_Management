package com.example.demo.service;


import com.example.demo.dto.DentistDto;
import com.example.demo.model.Dentist;

import java.util.List;
import java.util.Optional;

public interface DentistService {
    DentistDto save(DentistDto dentist);
    Optional<DentistDto> login(String email, String password);
}
