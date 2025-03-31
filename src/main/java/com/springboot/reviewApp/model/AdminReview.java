package com.springboot.reviewApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AdminReview {
    private Boolean accepted;
    private Boolean rejected;

}
