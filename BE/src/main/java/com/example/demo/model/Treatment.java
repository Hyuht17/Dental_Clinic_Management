package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="treatments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}
