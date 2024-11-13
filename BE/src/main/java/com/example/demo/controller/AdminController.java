package com.example.demo.controller;

import com.example.demo.dto.DentistDto;
import com.example.demo.service.DentistService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Optional;


@RestController
@AllArgsConstructor
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/admin")
public class AdminController {

    private DentistService dentistService;

    @PostMapping("/save")
    @Operation(summary = "Luu 1 dentist vao phong kham")
    public ResponseEntity<DentistDto> save(@RequestBody @Valid DentistDto dentistDto) {
        return ResponseEntity.ok(dentistService.save(dentistDto));
    }

    @PostMapping("/login")
    public ResponseEntity<DentistDto> login(@RequestParam String email, @RequestParam String password) {
        Optional<DentistDto> dentistDto = dentistService.login(email, password);
        if (dentistDto.isPresent()) {
            return ResponseEntity.ok(dentistDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
