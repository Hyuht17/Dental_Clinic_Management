
package com.example.demo.controller;

import com.example.demo.dto.DentistDto;
import com.example.demo.service.DentistService;
import lombok.RequiredArgsConstructor;
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
