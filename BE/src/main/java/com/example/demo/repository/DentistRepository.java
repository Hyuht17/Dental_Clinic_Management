package com.example.demo.repository;

import com.example.demo.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DentistRepository extends JpaRepository<Dentist, Integer> {
    Dentist findByDentistName(String username);
    Dentist findByEmail(String email);
    Dentist getDentistByUsername(String username);
}
