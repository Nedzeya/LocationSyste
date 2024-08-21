package com.klachkova.locationsystem.controllers;


import com.klachkova.locationsystem.dto.UserDTO;
import com.klachkova.locationsystem.modeles.User;
import com.klachkova.locationsystem.services.UserService;
import com.klachkova.locationsystem.util.converters.UserConverter;
import com.klachkova.locationsystem.util.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.klachkova.locationsystem.util.validators.ErrorsUtil.returnErrorsToClient;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;
    private final UserConverter userConverter;

    @Autowired
    public UserController(UserService userService,
                          UserValidator userValidator,
                          UserConverter userConverter) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.userConverter = userConverter;
    }


    @GetMapping()
    public List<UserDTO> getUser() {
        return userService.findAll()
                .stream()
                .map(userConverter::convertToDto)
                .collect(Collectors.toList());

    }


    @GetMapping("byId/{id}")
    public UserDTO getUser(@PathVariable("id") int id) {
        return userConverter.convertToDto(userService.findOne(id));
    }

    @GetMapping("byEmail/{email}")
    public UserDTO getUser(@PathVariable("email") String email) {
        return userConverter.convertToDto(userService.findOne(email).orElse(null));
    }


    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Validated @RequestBody UserDTO userDTO,
                                             BindingResult bindingResult) {

        User registeredUser = userConverter.convertToEntity(userDTO);
        userValidator.validate(registeredUser, bindingResult);

        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }

        userService.registerUser(registeredUser);

        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);

    }

}
