package com.klachkova.locationsystem.util.converters;

import com.klachkova.locationsystem.dto.LocationDTO;
import com.klachkova.locationsystem.modeles.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UserMapper.class)
public interface LocationMapper {

    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    @Mapping(target = "sharedUsers", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "owner", target = "owner")
    Location convertToEntity(LocationDTO locationDTO);

    @Mapping(source = "owner", target = "owner")
    LocationDTO convertToDto(Location location);
}
