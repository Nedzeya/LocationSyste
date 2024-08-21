package com.klachkova.locationsystem.repositories;

import com.klachkova.locationsystem.modeles.Location;
import com.klachkova.locationsystem.modeles.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location,Integer> {
    List<Location> findByOwner(User owner);
}
