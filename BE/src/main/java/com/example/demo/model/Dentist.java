package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@Data
@Getter
@Setter
@NoArgsConstructor
@Table(name = "dentists")
public class Dentist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_name", unique = true, nullable = false)
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "position")
    private String position;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private int status;

    @Column(name = "is_working")
    private int isWorking;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "dentist_role",
            joinColumns = @JoinColumn(name = "dentist_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    public Dentist(String userName, String password, String fullName, String position, String phone, String email, int isWorking, List<Role> roles) {
        this.userName = userName;
        this.password = password;
        this.fullName = fullName;
        this.position = position;
        this.phone = phone;
        this.email = email;
        this.isWorking = isWorking;
        this.roles = roles;
    }
}
