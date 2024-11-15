package com.example.demo.repository;

import com.example.demo.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DentistRepository extends JpaRepository<Dentist, Integer> {
    Dentist findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean getRoleByEmail(String email);

    @Query("SELECT r.id FROM Dentist d " +
            "JOIN d.roles r " +
            "WHERE d.email = :email")
    List<Integer> findRoleIdsByEmail(String email);
}
