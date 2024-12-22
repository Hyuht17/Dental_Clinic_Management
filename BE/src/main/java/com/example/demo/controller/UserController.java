package com.example.demo.controller;

import com.example.demo.dto.DentistDto;
import com.example.demo.dto.PatientDto;
import com.example.demo.service.PatientService;
import com.example.demo.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.demo.config.GenToken.generateToken;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/user")
public class UserController {
    private final PatientService patientService;

    private final S3Service S3Service;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = request.get("email");
            String password = request.get("password");

            if (patientService.login(email, password)) {
                // Tạo token (giả lập)
                String token = generateToken(email, password);

                response.put("success", true);
                response.put("token", token);
                response.put("userId", patientService.findPatientByEmail(email).getId());
            } else {
                response.put("success", false);
                response.put("message", "Invalid credentials");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = request.get("email");
            String password = request.get("password");
            String name = request.get("name");

            boolean isRegistered = patientService.register(email, password, name);

            if (isRegistered) {
                response.put("success", true);
                response.put("message", "Registration successful");
            } else {
                response.put("success", false);
                response.put("message", "Registration failed due to invalid credentials or email already registered");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(@RequestHeader(value = "token", required = false) String token,
                                                                @RequestParam(value = "userId", required = false) Integer userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate the token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            // Fetch profile using service
            PatientDto profileData = patientService.findPatientById(userId);

            if (profileData != null) {
                response.put("success", true);
                response.put("userData", profileData);
            } else {
                response.put("success", false);
                response.put("message", "Profile not found");
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateDoctorProfile(
            @RequestHeader(value = "token", required = false) String token,
            @RequestParam("userId") int userId,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("sex") String gender,
            @RequestParam("dob") String dobString, // Ngày dưới dạng chuỗi
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            // Parse ngày sinh (dobString -> Date)
            Date dob;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dob = dateFormat.parse(dobString);
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "Invalid date format. Expected format: yyyy-MM-dd");
                return ResponseEntity.badRequest().body(response);
            }


            // Tìm thông tin bệnh nhân
            PatientDto patient = patientService.findPatientById(userId);
            // Upload hình ảnh nếu có
            String imageUrl = patient.getImage();
            if (image != null && !image.isEmpty()) {
                imageUrl = S3Service.uploadFile(image);
            }

            if (patient != null) {
                // Cập nhật thông tin
                patient.setFullName(fullName);
                patient.setPhone(phone);
                patient.setAddress(address);
                patient.setImage(imageUrl);
                patient.setSex(gender);
                patient.setDob(dob);

                // Lưu thông tin
                patientService.update(patient);

                response.put("success", true);
                response.put("message", "Profile updated successfully.");
            } else {
                response.put("success", false);
                response.put("message", "Patient not found.");
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }

}
