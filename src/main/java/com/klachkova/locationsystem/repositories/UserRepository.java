package com.klachkova.locationsystem.repositories;

import com.klachkova.locationsystem.modeles.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing User entities in the database.
 * <p>
 * Provides methods to perform CRUD operations on User entities and additional
 * methods to check for the existence of a user by email and to find a user by email.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Checks if a user with the given email exists in the database.
     *
     * @param email the email address of the user
     * @return true if a user with the given email exists, otherwise false
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their unique email address.
     *
     * @param email the email address of the user
     * @return an Optional containing the user if found, otherwise an empty Optional
     */
    Optional<User> findByEmail(String email);
}

