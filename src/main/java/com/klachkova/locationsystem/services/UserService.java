package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.modeles.User;
import com.klachkova.locationsystem.repositories.UserRepository;
import com.klachkova.locationsystem.util.NotCreatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(User user) {
        String email = user.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new NotCreatedException("User with this email already exists.");
                   }
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User with email " + email + " not found"));
    }
}


