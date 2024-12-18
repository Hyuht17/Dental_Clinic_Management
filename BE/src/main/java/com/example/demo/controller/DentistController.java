package com.example.demo.controller;


import com.example.demo.service.DentistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.example.demo.controller.GenToken.generateToken;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/doctor")
public class DentistController {
    private final DentistService dentistService;

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

                response.put("success", true);
                response.put("token", token);
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
}
