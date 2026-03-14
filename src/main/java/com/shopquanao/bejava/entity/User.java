package com.shopquanao.bejava.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "employee_code", length = 20)
    private String employeeCode;

    @Column(name = "id_card", length = 20)
    private String idCard;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "leave_date")
    private LocalDate leaveDate;
}
