package com.klachkova.locationsystem.util.validators;

import com.klachkova.locationsystem.modeles.Location;
import com.klachkova.locationsystem.modeles.User;
import com.klachkova.locationsystem.services.LocationService;
import com.klachkova.locationsystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LocationValidator implements Validator {
    private final LocationService locationService;
    private final UserService userService;

    @Autowired
    public LocationValidator(LocationService locationService, UserService userService) {
        this.locationService = locationService;
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Location.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Location location = (Location) target;
        User owner = location.getOwner();

        if (owner == null) return;
        if (!userService.existsByEmail(owner.getEmail())) {
            errors.rejectValue("user", "",
                    "No such user in data base");
        }


    }
}
