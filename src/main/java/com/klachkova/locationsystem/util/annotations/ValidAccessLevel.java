package com.klachkova.locationsystem.util.annotations;

import com.klachkova.locationsystem.util.validators.AccessLevelValidator;
import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccessLevelValidator.class)
public @interface ValidAccessLevel {
    String message() default "Invalid access level";
}
