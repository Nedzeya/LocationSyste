package com.klachkova.locationsystem.repositories;

import com.klachkova.locationsystem.modeles.Location;
import com.klachkova.locationsystem.modeles.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing Location entities in the database.
 * <p>
 * Provides methods to perform CRUD operations on Location entities and additional
 * methods to check for the existence of a location by its address, find a location
 * by address, and retrieve all locations owned by a specific user.
 * </p>
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    /**
     * Checks if a location with the given address exists in the database.
     *
     * @param address the address of the location
     * @return true if a location with the given address exists, otherwise false
     */
    boolean existsByAddress(String address);

    /**
     * Finds a location by its unique address.
     *
     * @param locationAddress the address of the location
     * @return an Optional containing the location if found, otherwise an empty Optional
     */
    Optional<Location> findByAddress(String locationAddress);

    /**
     * Finds all locations that are owned by the specified user.
     *
     * @param owner the user who owns the locations
     * @return a list of locations owned by the specified user
     */
    List<Location> findAllByOwner(User owner);
}
