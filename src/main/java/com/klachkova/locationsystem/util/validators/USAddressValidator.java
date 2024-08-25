package com.klachkova.locationsystem.util.validators;

import com.klachkova.locationsystem.util.annotations.USAddress;
import org.springframework.stereotype.Component;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@Component
public class USAddressValidator implements ConstraintValidator<USAddress, String> {
    private static final String US_ADDRESS_REGEX = "^(\\d+)\\s([\\w\\s]+),\\s([\\w\\s]+),\\s([A-Z]{2}),\\s(\\d{5})(-\\d{4})?$";

    @Override
    public void initialize(USAddress constraintAnnotation) {
    }

    @Override
    public boolean isValid(String address, ConstraintValidatorContext context) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(US_ADDRESS_REGEX, address);
    }
}
