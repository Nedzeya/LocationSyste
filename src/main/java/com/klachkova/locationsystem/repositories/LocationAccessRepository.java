package com.klachkova.locationsystem.repositories;

import com.klachkova.locationsystem.modeles.Location;
import com.klachkova.locationsystem.modeles.LocationAccess;
import com.klachkova.locationsystem.modeles.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 * Repository interface for accessing LocationAccess entities in the database.
 * <p>
 * Provides methods to perform CRUD operations on LocationAccess entities and additional
 * methods to find access records by user, location, or combination of both.
 * </p>
 */
@Repository
public interface LocationAccessRepository extends JpaRepository<LocationAccess, Integer> {
    /**
     * Finds all LocationAccess records associated with a specific user.
     *
     * @param user the user whose access records are to be retrieved
     * @return a list of LocationAccess records associated with the specified user
     */
    List<LocationAccess> findByUser(User user);
    /**
     * Finds all LocationAccess records associated with a specific location ID.
     *
     * @param locationId the ID of the location whose access records are to be retrieved
     * @return a list of LocationAccess records associated with the specified location ID
     */
    List<LocationAccess> findByLocationId(int locationId);
    /**
     * Finds a LocationAccess record that matches a specific location and user.
     *
     * @param location the location to match
     * @param user the user to match
     * @return an Optional containing the LocationAccess record if found, otherwise an empty Optional
     */
    Optional<LocationAccess> findByLocationAndUser(Location location, User user);
}
