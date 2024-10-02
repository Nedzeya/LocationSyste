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

/**
 * Service class for managing User entities.
 * <p>
 * Provides methods to register a new user, find users by ID or email, and validate user data.
 * </p>
 */
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

    /**
     * Registers a new user.
     * <p>
     * Converts the provided UserDTO to a User entity, validates it, and saves it to the repository.
     * </p>
     *
     * @param userDTO the data transfer object representing the user to register
     * @return the registered user as a UserDTO
     * @throws NotCreatedException if a user with the same email already exists
     * @throws ValidationException if the user data is invalid
     */
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

    /**
     * Validates the provided User entity.
     * <p>
     * Checks if the User entity adheres to the validation constraints and throws an exception if any violations are
     * found.
     * </p>
     *
     * @param user the User entity to validate
     * @throws ValidationException if validation constraints are violated
     */
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

    /**
     * Retrieves all users.
     *
     * @return a list of all User entities
     */
    public List<User> findAll() {

        return userRepository.findAll();
    }

    /**
     * Retrieves a user by its ID.
     * <p>
     * Throws an exception if no user with the given ID is found.
     * </p>
     *
     * @param id the ID of the user to retrieve
     * @return the User entity with the given ID
     * @throws NotFoundException if no user with the given ID is found
     */
    public User findById(int id) {

        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    /**
     * Retrieves a user by its unique email.
     * <p>
     * Throws an exception if no user with the given email is found.
     * </p>
     *
     * @param email the email of the user to retrieve
     * @return the User entity with the given email
     * @throws NotFoundException if no user with the given email is found
     */
    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
    }
}