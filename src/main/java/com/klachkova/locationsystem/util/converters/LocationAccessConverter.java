package com.klachkova.locationsystem.util.converters;

import com.klachkova.locationsystem.dto.LocationAccessDTO;
import com.klachkova.locationsystem.modeles.LocationAccess;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationAccessConverter {
    private final ModelMapper modelMapper;

    @Autowired
    public LocationAccessConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public LocationAccess convertToEntity(LocationAccessDTO locationAccessDTO) {

        return modelMapper.map(locationAccessDTO, LocationAccess.class);
    }

    public LocationAccessDTO convertToDto(LocationAccess locationAccess) {

        return modelMapper.map(locationAccess, LocationAccessDTO.class);
    }

}
