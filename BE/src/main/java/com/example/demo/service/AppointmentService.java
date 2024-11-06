package com.example.demo.service;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.model.Appointment;

import java.util.Date;
import java.util.List;

public interface AppointmentService {
    AppointmentDto save(AppointmentDto appointment);
    List<AppointmentDto> getAll();
}
