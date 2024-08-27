package com.klachkova.locationsystem.repositories;

import com.klachkova.locationsystem.modeles.Location;
import com.klachkova.locationsystem.modeles.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    boolean existsByAddress(String address);
    Optional<Location> findByAddress(String locationAddress);
    List<Location> findAllByOwner(User owner);
}
