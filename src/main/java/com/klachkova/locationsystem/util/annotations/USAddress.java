package com.klachkova.locationsystem.util.annotations;

import com.klachkova.locationsystem.util.validators.USAddressValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = USAddressValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface USAddress {

    String message() default "Invalid US address format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
