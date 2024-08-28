package com.klachkova.locationsystem.util.validators;

import com.klachkova.locationsystem.modeles.AccessLevel;
import com.klachkova.locationsystem.util.annotations.ValidAccessLevel;
import org.springframework.stereotype.Component;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class AccessLevelValidator implements ConstraintValidator<ValidAccessLevel,AccessLevel> {

    @Override
    public void initialize(ValidAccessLevel constraintAnnotation) {
    }

    @Override
    public boolean isValid(AccessLevel value, ConstraintValidatorContext context) {
        if (value == null) {
            context.buildConstraintViolationWithTemplate("Access level must not be null")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }

        // AccessLevel is an enum, so this check is redundant, but kept for completeness
        try {
            AccessLevel.valueOf(value.name());
            return true;
        } catch (IllegalArgumentException e) {
            context.buildConstraintViolationWithTemplate("Invalid access level. It can be READ_ONLY or ADMIN")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }
    }
}