package com.example.demo.service;

import com.example.demo.dto.MedicineDto;
import java.util.List;
public interface MedicineService {
    MedicineDto save(MedicineDto medicine);
    List<MedicineDto> getAll();
}
