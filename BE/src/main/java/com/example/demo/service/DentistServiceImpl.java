package com.example.demo.service;

import com.example.demo.config.UserAlreadyExistsException;
import com.example.demo.dto.AppointmentDto;
import com.example.demo.dto.DentistDto;
import com.example.demo.model.Appointment;
import com.example.demo.model.Dentist;
import com.example.demo.model.Role;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.DentistRepository;
import com.example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DentistServiceImpl implements DentistService {


    private DentistRepository dentistRepository;

    private RoleRepository roleRepository;

    @Override
    public Dentist save(DentistDto registrationDto) {
        if (dentistRepository.findByDentistName(registrationDto.getUserName()) != null || dentistRepository.findByEmail(registrationDto.getEmail()) != null) {
            throw new UserAlreadyExistsException("Dentist with this email already exists.");
        }
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role("ROLE_USER");
            userRole = roleRepository.save(userRole);
        }
//        String password = passwordEncoder.encode(registrationDto.getPassword());
//        registrationDto.setPassword(password);
        Dentist user = new Dentist(registrationDto.getUserName(),
                registrationDto.getPassword(),
                registrationDto.getFullName(),
                registrationDto.getPosition(),
                registrationDto.getPhone(),
                registrationDto.getEmail(),
                registrationDto.getIsWorking(),
                Arrays.asList(userRole));
        return dentistRepository.save(user);
    }

    @Override
    public Boolean checkPasswordUser(String username, String password) {
        Dentist user = dentistRepository.findByDentistName(username);
        if (user.getPassword().equals(password)) return true;
        return false;
    }

    @Override
    public Boolean checkUsernameUser(String username) {
        Dentist user = dentistRepository.findByDentistName(username);
        if (user==null) return false;
        return true;
    }

    @Override
    public Dentist getUserByUsername(String username) {
        return dentistRepository.getDentistByUsername(username);
    }

}
