package com.klachkova.locationsystem.util.validators;

import com.klachkova.locationsystem.modeles.User;
import com.klachkova.locationsystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.stereotype.Component;

@Component
public class UserValidator implements Validator  {
    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (userService.findOne(user.getEmail()).isPresent()){
            errors.rejectValue("email", "","A user with that email already exists");
        }

    }
}
