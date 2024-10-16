package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="medicines")
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}
