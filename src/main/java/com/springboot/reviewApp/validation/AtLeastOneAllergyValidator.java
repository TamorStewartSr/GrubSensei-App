package com.springboot.reviewApp.validation;

import com.springboot.reviewApp.model.ReviewUser;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AtLeastOneAllergyValidator implements ConstraintValidator<AtLeastOneAllergy, ReviewUser> {
    @Override
    public boolean isValid(ReviewUser user, ConstraintValidatorContext context) {
        if (user == null) {
            return false;
        }

        return  Boolean.TRUE.equals(user.getPeanutAllergies()) ||
                Boolean.TRUE.equals(user.getEggAllergies()) ||
                Boolean.TRUE.equals(user.getDairyAllergies());
    }

}
