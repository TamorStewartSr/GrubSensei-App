package com.springboot.reviewApp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;



@RequiredArgsConstructor
@Getter
@Setter
@Entity
public class Restaurant {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank(message = "Restaurant name is required")
    private String name;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "State is required")
    private String state;
    @Pattern(regexp = "\\d{5}", message = "Zip Code must be 5 digits")
    private String zipCode;
    private String website;
    private String phoneNumber;
    @Column(nullable = false)
    private String overallScore = "";
    @Column(nullable = false)
    private String peanutScore = "";
    @Column(nullable = false)
    private String eggScore = "";
    @Column(nullable = false)
    private String dairyScore = "";

}

