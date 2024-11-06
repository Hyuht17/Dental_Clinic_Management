package com.example.demo.service;


import com.example.demo.dto.DentistDto;
import com.example.demo.model.Dentist;

import java.util.List;

public interface DentistService {
    Dentist save(DentistDto dentist);
    Boolean checkPasswordUser(String username, String password);
    Boolean checkUsernameUser(String username);
    Dentist getUserByUsername(String username);
}
