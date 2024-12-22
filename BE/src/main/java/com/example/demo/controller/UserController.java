package com.example.demo.controller;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.dto.DentistDto;
import com.example.demo.dto.PatientDto;
import com.example.demo.dto.TreatmentDto;
import com.example.demo.model.Treatment;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Time;
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

    private final AppointmentService appointmentService;

    private final DentistService dentistService;

    private final TreatmentService treatmentService;

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

    @GetMapping("/appointments")
    public ResponseEntity<Map<String, Object>> getAppointments(@RequestHeader(value = "token", required = false) String token,
                                                               @RequestParam(value = "userId", required = false) Integer userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate the token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            // Fetch appointments using service
            response.put("success", true);
            response.put("appointments", appointmentService.findAppointmentsByPatientId(userId));

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel-appointment")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@RequestBody Map<String, Integer> request,
                                                                 @RequestHeader(value = "token", required = false) String token)
    {
        try {
            Map<String, Object> response = new HashMap<>();
            // Kiểm tra token nếu cần thiết (tùy vào yêu cầu bảo mật của bạn)
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }

            int appointmentId = Integer.parseInt(request.get("appointmentId").toString());
            AppointmentDto appointment = appointmentService.findAppointmentById(appointmentId);
            if (appointment == null) {
                Map<String, Object> notFoundResponse = new HashMap<>();
                notFoundResponse.put("success", false);
                notFoundResponse.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
            }

            appointment.setCancelled(true);
            appointmentService.save(appointment);

            response.put("success", true);
            response.put("message", "Appointment cancelled successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/book-appointment")
    public ResponseEntity<Map<String, Object>> bookAppointment(@RequestBody Map<String, String> request,
                                                               @RequestHeader(value = "token", required = false) String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate the token
            if (token == null || token.isEmpty()) {
                response.put("success", false);
                response.put("message", "Token is missing or invalid");
                return ResponseEntity.badRequest().body(response);
            }
            int dentistId = Integer.parseInt(request.get("dentistId").toString());
            String slotDate = request.get("slotDate").toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Định dạng ngày phù hợp
            Date appointmentDate = sdf.parse(slotDate);
            Time appointmentTime = Time.valueOf(request.get("slotTime").toString());
            int patientId = Integer.parseInt(request.get("userId").toString());

            AppointmentDto appointmentDto = new AppointmentDto();
            appointmentDto.setPatientId(patientId);
            appointmentDto.setAppointmentDate(appointmentDate);
            appointmentDto.setAppointmentTime(appointmentTime);
            appointmentDto.setCancelled(false);
            appointmentDto.setIsCompleted(false);
            DentistDto dentist = dentistService.findDentistById(dentistId);
            appointmentDto.setDentist(dentist);

            TreatmentDto treatmentDto = new TreatmentDto();
            treatmentDto.setFees(100);
            treatmentDto.setPatientId(patientId);
            treatmentDto.setDentistId(dentistId);

            // Save the appointment and treatment
            appointmentService.save(appointmentDto);
            treatmentService.save(treatmentDto);

            response.put("success", true);
            response.put("message", "Appointment booked successfully");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok(response);
    }
}
