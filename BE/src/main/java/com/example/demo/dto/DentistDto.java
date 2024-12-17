package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DentistDto {
    private int dentistId;
    private String imgUrl;
    private String name;
    private String email;
    private String password;
    private String fees;
    private String speciality;
    private int status;
    private List<String> roles;

    public DentistDto(int id, String name, String imgUrl) {
        this.dentistId = id;
        this.name = name;
        this.imgUrl = imgUrl;
    }
}
