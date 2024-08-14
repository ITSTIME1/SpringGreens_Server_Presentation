package com.spring_greens.presentation.auth.entity;

import com.spring_greens.presentation.global.enums.Role;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "contact", length = 20)
    private String contact;

    @Column(name = "business_number", length = 20)
    private String businessNumber;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "alert_type")
    private boolean alertType;

    @Column(name = "terms_type")
    private boolean termsType;

    @Column(name = "social_type")
    private boolean socialType;

    @Column(name = "social_name")
    private String socialName;

    @Column(name = "road_address", length = 200)
    private String roadAddress;

    @Column(name = "address_details", length = 200)
    private String addressDetails;

    @Column(name = "password")
    private String password;

    @Builder
    public User(Long id, Role role, String email, String contact, String businessNumber,
                String name, boolean alertType, boolean termsType, boolean socialType,
                String socialName, String roadAddress, String addressDetails, String password) {
        this.id = id;
        this.role = role;
        this.email = email;
        this.contact = contact;
        this.businessNumber = businessNumber;
        this.name = name;
        this.alertType = alertType;
        this.termsType = termsType;
        this.socialType = socialType;
        this.socialName = socialName;
        this.roadAddress = roadAddress;
        this.addressDetails = addressDetails;
        this.password = password;
    }

    public User updateUserInfo(String email, String name) {
        this.email = email;
        this.name = name;
        return this;
    }
}