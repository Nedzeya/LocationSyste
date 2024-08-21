package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.dto.UserDTO;
import com.klachkova.locationsystem.modeles.User;
import com.klachkova.locationsystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
@Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll(){
    return userRepository.findAll();
    }

    public User findOne (int id){
    return userRepository.findById(id).get();
    }

    public Optional<User> findOne (String email){
    return userRepository.findByEmail (email);
    }

    @Transactional
    public void registerUser (User user) {
        userRepository.save(user);
    }

}


