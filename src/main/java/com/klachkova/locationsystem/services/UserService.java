package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.dto.UserDTO;
import com.klachkova.locationsystem.modeles.User;
import com.klachkova.locationsystem.repositories.UserRepository;
import com.klachkova.locationsystem.util.converters.UserConverter;
import com.klachkova.locationsystem.util.exceptions.NotCreatedException;
import com.klachkova.locationsystem.util.exceptions.NotFoundException;
import com.klachkova.locationsystem.util.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;


@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final Validator validator;
    private final UserConverter userConverter;


    @Autowired
    public UserService(UserRepository userRepository, Validator validator, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.userConverter = userConverter;
    }

    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        User userToRegister = userConverter.convertToEntity(userDTO);
        validateUser(userToRegister);
        String email = userToRegister.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new NotCreatedException("User with this email already exists.");
        }
        User registeredUser = userRepository.save(userToRegister);
        return userConverter.convertToDto(registeredUser);
    }

    private void validateUser(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<User> violation : violations) {
                sb.append(violation.getMessage()).append(" ");
            }
            throw new ValidationException(sb.toString().trim());
        }
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
    }
}