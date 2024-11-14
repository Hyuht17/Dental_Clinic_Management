package com.example.demo.controller;

import com.example.demo.dto.DentistDto;
import com.example.demo.dto.LoginDto;
import com.example.demo.service.DentistService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/admin")
public class AdminController {

    private final DentistService dentistService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/add-doctor")
    @Operation(summary = "Luu 1 dentist vao phong kham")
    public ResponseEntity<?> save(@RequestPart("image") MultipartFile imageFile,
                                  @RequestPart("name") String name,
                                  @RequestPart("email") String email,
                                  @RequestPart("password") String password,
                                  @RequestPart("fees") String fees,
                                  @RequestPart("speciality") String speciality) {
        try {
            if (imageFile == null || imageFile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image file is required.");
            }
            String fileName = imageFile.getOriginalFilename();
            DentistDto dentistDto = DentistDto.builder()
                    .name(name)
                    .email(email)
                    .password(password)
                    .fees(fees)
                    .speciality(speciality)
                    .imgUrl(fileName)
                    .build();
            return ResponseEntity.ok(dentistService.save(dentistDto));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDto login) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<DentistDto> dentistDto = dentistService.login(login.getEmail(), login.getPassword());

            if (dentistDto.isPresent()) {
                String token = Jwts.builder()
                        .setSubject(dentistDto.get().getEmail())
                        .signWith(SignatureAlgorithm.HS256, jwtSecret)
                        .compact();

                // Đảm bảo mã hóa Base64 được thực hiện đúng cách
                String encodedToken = Base64.getEncoder().encodeToString(token.getBytes());

                response.put("success", true);
                response.put("token", encodedToken);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
