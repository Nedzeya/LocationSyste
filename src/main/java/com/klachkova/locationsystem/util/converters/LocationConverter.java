package com.klachkova.locationsystem.util.converters;

import com.klachkova.locationsystem.dto.LocationDTO;
import com.klachkova.locationsystem.modeles.Location;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationConverter {
    private final ModelMapper modelMapper;

    @Autowired
    public LocationConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Location convertToEntity(LocationDTO locationDTO){
        return modelMapper.map(locationDTO, Location.class);
    }

    public LocationDTO convertToDto(Location location){
        return modelMapper.map(location, LocationDTO.class);
    }

}
