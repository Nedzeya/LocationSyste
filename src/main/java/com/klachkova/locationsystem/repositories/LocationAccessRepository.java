package com.klachkova.locationsystem.repositories;

import com.klachkova.locationsystem.modeles.LocationAccess;
import com.klachkova.locationsystem.modeles.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationAccessRepository extends JpaRepository<LocationAccess, Integer> {
    List<LocationAccess> findByUser(User user);
    List<LocationAccess> findByLocationId(int locationId);
    Optional<LocationAccess> findByLocationIdAndUser(int locationId, User user);
}
