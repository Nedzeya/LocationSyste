package com.klachkova.locationsystem.util.validators;

import com.klachkova.locationsystem.modeles.Location;
import com.klachkova.locationsystem.modeles.LocationAccess;
import com.klachkova.locationsystem.modeles.User;
import com.klachkova.locationsystem.services.LocationService;
import com.klachkova.locationsystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LocationAccessValidator implements Validator {
    private final UserService userService;
    private final LocationService locationService;

    @Autowired
    public LocationAccessValidator(UserService userService, LocationService locationService) {
        this.userService = userService;
        this.locationService = locationService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return LocationAccess.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LocationAccess locationAccess = (LocationAccess) target;

        User user = locationAccess.getUser();
        if (user == null) return;
        if (!userService.existsByEmail(user.getEmail())) {
            errors.rejectValue("user", "",
                    "No such user in data base");
        }

       Location location = locationAccess.getLocation();
        if (location == null) return;
        if (!locationService.existsByAddress(location.getAddress())) {
            errors.rejectValue("location", "",
                    "No such location in data base");
        }
    }
}
