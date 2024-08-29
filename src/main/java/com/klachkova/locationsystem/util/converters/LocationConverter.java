package com.klachkova.locationsystem.util.converters;

import com.klachkova.locationsystem.dto.LocationDTO;
import com.klachkova.locationsystem.modeles.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationConverter {
    private final LocationMapper locationMapper;

    @Autowired
    public LocationConverter() {
        this.locationMapper = LocationMapper.INSTANCE;
    }
    public Location convertToEntity(LocationDTO locationDTO){
        return locationMapper.convertToEntity(locationDTO);
    }
    public LocationDTO convertToDto(Location location){
        return locationMapper.convertToDto(location);
    }
}
