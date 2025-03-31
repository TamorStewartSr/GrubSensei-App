package com.springboot.reviewApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
public class DiningReview {
    @Id
    @GeneratedValue
    private Long id;
    private String submittedBy;
    private Long restaurantId;
    private Integer peanutScore;
    private Integer eggScore;
    private Integer dairyScore;
    @NotBlank(message = "Please leave a comment")
    private String comment;
    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus;

}
