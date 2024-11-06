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
    private String userName;
    private String password;
    private String fullName;
    private String position;
    private String phone;
    private String email;
    private int status;
    private int isWorking;
    private List<String> roles;
}
