
package com.example.demo.controller;

import com.example.demo.dto.DentistDto;
import com.example.demo.service.DentistService;
import com.example.demo.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.SpringVersion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static com.example.demo.controller.GenToken.generateToken;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/admin")
public class AdminController {

    private final DentistService dentistService;

    private final S3Service S3Service;

    @PostMapping("/add-doctor")
    @Operation(summary = "Luu 1 dentist vao phong kham")
    public ResponseEntity<?> save(@RequestPart("image") MultipartFile imageFile,
                                  @RequestPart("name") String name,
                                  @RequestPart("email") String email,
                                  @RequestPart("password") String password,
                                  @RequestPart("fees") String fees,
                                  @RequestPart("speciality") String speciality) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (imageFile == null || imageFile.isEmpty()) {
                response.put("success", false);
                response.put("message", "Image file is required.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            String imageUrl = S3Service.uploadFile(imageFile);
            DentistDto dentistDto = DentistDto.builder()
                    .name(name)
                    .email(email)
                    .password(password)
                    .fees(fees)
                    .speciality(speciality)
                    .imgUrl(imageUrl)
                    .build();
            dentistService.save(dentistDto);
            response.put("success", true);
            response.put("message", "Dentist added successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "An error occurred while processing the request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginAdmin(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = request.get("email");
            String password = request.get("password");
            // Kiểm tra role của người dùng
            if (!dentistService.checkRole(email)) {
                response.put("success", false);
                response.put("message", "Không đủ quyền");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);  // Trả về 403 Forbidden
            }
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
}
