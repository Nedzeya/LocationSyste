package com.klachkova.locationsystem.util.converters;

import com.klachkova.locationsystem.dto.LocationDTO;
import com.klachkova.locationsystem.modeles.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converter for converting between Location entities and LocationDTO objects.
 * <p>
 * This component uses LocationMapper to perform conversions between entities and DTOs.
 * </p>
 */
@Component
public class LocationConverter {
    private final LocationMapper locationMapper;

    @Autowired
    public LocationConverter() {
        this.locationMapper = LocationMapper.INSTANCE;
    }

    /**
     * Converts a LocationDTO to a Location entity.
     *
     * @param locationDTO the LocationDTO object to convert
     * @return the converted Location entity
     */
    public Location convertToEntity(LocationDTO locationDTO) {
        return locationMapper.convertToEntity(locationDTO);
    }

    /**
     * Converts a Location entity to a LocationDTO.
     *
     * @param location the Location entity to convert
     * @return the converted LocationDTO object
     */
    public LocationDTO convertToDto(Location location) {
        return locationMapper.convertToDto(location);
    }
}
