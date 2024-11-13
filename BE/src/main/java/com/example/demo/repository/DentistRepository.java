package com.example.demo.repository;

import com.example.demo.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DentistRepository extends JpaRepository<Dentist, Integer> {
    Dentist findByEmail(String email);
}
