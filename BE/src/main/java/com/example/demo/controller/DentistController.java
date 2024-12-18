package com.example.demo.controller;


import com.example.demo.dto.AppointmentDto;
import com.example.demo.dto.PatientDto;
import com.example.demo.model.Appointment;
import com.example.demo.model.Dentist;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.DentistService;
import com.example.demo.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.demo.controller.GenToken.generateToken;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/doctor")
public class DentistController {
    private final DentistService dentistService;

    private final PatientService patientService;

    private final AppointmentService appointmentService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginDentist(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = request.get("email");
            String password = request.get("password");
//            // Kiểm tra có phải role admin không
//            if (!dentistService.checkRole(email)) {
//                response.put("success", false);
//                response.put("message", "Không đủ quyền");
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);  // Trả về 403 Forbidden
//            }
            if (dentistService.login(email, password) == true) {
                // Tạo token sử dụng hàm tự viết
                String token = generateToken(email, password);

                Map<String, Object> data = new HashMap<>();
                response.put("success", true);
                response.put("token", token);
                response.put("dentistId", dentistService.findDentistByEmail(email).getDentistId());

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

    @GetMapping("/update-profile")
    public ResponseEntity<Map<String, Object>> getDoctorProfile(@RequestHeader(value = "aToken", required = false) String aToken) {

    return null;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@RequestHeader(value = "dToken", required = false) String dToken,
                                                            @RequestParam(value = "dentistId", required = false) Integer dentistId) {
        try{
            if (validateToken(dToken)) {
                Map<String, Object> response = new HashMap<>();
            }
            List<AppointmentDto> appointments = appointmentService.findAppointmentByDentistId(dentistId);
            List<PatientDto> patients = patientService.findPatientByDentistId(dentistId);

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> dashData = new HashMap<>();

            dashData.put("appointments", appointments.size());
            dashData.put("patients", patients.size());
            dashData.put("latestAppointments", appointments);

            response.put("success", true);
            response.put("dashData", dashData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/cancel-appointment")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@RequestHeader(value = "dToken", required = false) String dToken,
                                                                 @RequestBody Map<String, Object> request) {
        try {
            if (!validateToken(dToken)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                return ResponseEntity.ok(response);
            }
            int appointmentId = (int) request.get("appointmentId");
            AppointmentDto appointment = appointmentService.findAppointmentById(appointmentId);

            appointment.setCancelled(true);
            appointmentService.save(appointment);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Appointment cancelled successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/complete-appointment")
    public ResponseEntity<Map<String, Object>> completeAppointment(@RequestHeader(value = "dToken", required = false) String dToken,
                                                                   @RequestBody Map<String, Object> request) {
        try {
            if (!validateToken(dToken)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                return ResponseEntity.ok(response);
            }
            int appointmentId = (int) request.get("appointmentId");
            AppointmentDto appointment = appointmentService.findAppointmentById(appointmentId);

            appointment.setIsCompleted(true);
            appointmentService.save(appointment);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Appointment completed successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/appointments")
    public ResponseEntity<Map<String, Object>> getAppointments(@RequestHeader(value = "dToken", required = false) String dToken,
                                                               @RequestParam(value = "dentistId", required = false) Integer dentistId) {
        try {
            if (!validateToken(dToken)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                return ResponseEntity.ok(response);
            }
            List<AppointmentDto> appointments = appointmentService.findAppointmentByDentistId(dentistId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("appointments", appointments);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean validateToken(String token) {
        // Thêm logic để xác thực token tại đây
        return true; // Trả về true nếu hợp lệ, false nếu không
    }
}
