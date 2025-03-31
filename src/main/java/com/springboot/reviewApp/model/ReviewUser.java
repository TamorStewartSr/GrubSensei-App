package com.springboot.reviewApp.model;

import com.springboot.reviewApp.validation.AtLeastOneAllergy;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@RequiredArgsConstructor
@Setter
@Getter
@Entity
@AtLeastOneAllergy(message = "You must select at least one allergy.")
public class ReviewUser {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank(message = "DisplayName cannot be empty")
    private String displayName;
    @NotBlank(message = "Password cannot be empty")
    private String password;
    @NotBlank(message = "City cannot be empty")
    private String city;
    @NotBlank(message = "State cannot be empty")
    private String state;
    @NotBlank(message = "ZipCode cannot be empty")
    private String zipCode;

    private Boolean peanutAllergies;
    private Boolean eggAllergies;
    private Boolean dairyAllergies;

}
