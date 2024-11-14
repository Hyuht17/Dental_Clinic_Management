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

    @Autowired
    private DentistRepository dentistRepository;
    @Autowired
    private RoleRepository roleRepository;

    private final ModelMapper modelMapper;

//    @Autowired
//    private PasswordEncoder passwordEncoder;


    @Override
    public DentistDto save(DentistDto registrationDto) {
        if (dentistRepository.findByEmail(registrationDto.getEmail()) != null) {
            throw new UserAlreadyExistsException("Dentist with this email already exists.");
        }
        Role userRole = roleRepository.findByName("ROLE_DENTIST");
        if (userRole == null) {
            userRole = new Role("ROLE_DENTIST");
            userRole = roleRepository.save(userRole);
        }
//        String password = passwordEncoder.encode(registrationDto.getPassword());
//        registrationDto.setPassword(password);
        Dentist user = new Dentist(registrationDto.getImgUrl(),
                registrationDto.getName(),
                registrationDto.getEmail(),
                registrationDto.getPassword(),
                registrationDto.getFees(),
                registrationDto.getSpeciality(),
                Arrays.asList(userRole));
        Dentist savedDentist = dentistRepository.save(user);
        return modelMapper.map(savedDentist, DentistDto.class);
    }

    @Override
    public Optional<DentistDto> login(String email, String password) {
        Dentist dentist = dentistRepository.findByEmail(email);
        if (dentist != null && dentist.getPassword().equals(password)) {
            return Optional.of(modelMapper.map(dentist, DentistDto.class));
        }
        return Optional.empty();
    }

}
