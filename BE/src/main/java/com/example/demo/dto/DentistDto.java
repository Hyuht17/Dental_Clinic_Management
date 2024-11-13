package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DentistDto {
    private int dentistId;
    private String imgUrl;
    private String name;
    private String email;
    private String password;
    private String fees;
    private String speciality;
    private List<String> roles;
}
