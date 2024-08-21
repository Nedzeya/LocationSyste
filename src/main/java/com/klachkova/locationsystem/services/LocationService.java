package com.klachkova.locationsystem.services;

import com.klachkova.locationsystem.modeles.Location;
import com.klachkova.locationsystem.modeles.User;
import com.klachkova.locationsystem.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserService userService;

    //   private final LocationAccessRepository locationAccessRepository;


    @Autowired
    public LocationService(LocationRepository locationRepository, UserService userService) {
        this.locationRepository = locationRepository;
        this.userService = userService;
    }

    @Transactional
    public void registerLocation(Location location) {
        updateLocation(location);
        locationRepository.save(location);
    }

    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    public Location findOne(int id) {
        return locationRepository.findById(id).get();
    }

    public void deleteLocation(int id) {
        locationRepository.deleteById(id);
    }

    public void updateLocation(Location locationDetails) {

        String ownerEmail = locationDetails.getOwner().getEmail();
        locationDetails.setOwner(userService.findOne(ownerEmail));

    }
  /*
    public List<LocationAccess> getLocationAccesses(int id) {
        return locationAccessRepository.findAll();
    }


    public void manageAccess(int locationId, int userId, boolean adminAccess) {
        LocationAccess locationAccess = new LocationAccess();
        locationAccess.setLocation(locationRepository.findById(locationId)
                .orElseThrow (() -> new RuntimeException("Location not found with id " + locationId));
        locationAccess.setUser(userRepository.findById(userId).orElseThrow());
        locationAccess.setAdminAccess(adminAccess);
        locationAccessRepository.save(locationAccess);
    }
 */
}
