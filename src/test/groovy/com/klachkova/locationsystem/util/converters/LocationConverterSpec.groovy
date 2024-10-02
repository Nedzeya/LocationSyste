package com.klachkova.locationsystem.util.converters

import com.klachkova.locationsystem.dto.LocationDTO
import com.klachkova.locationsystem.dto.UserDTO
import com.klachkova.locationsystem.modeles.Location
import com.klachkova.locationsystem.modeles.User
import spock.lang.Specification

class LocationConverterSpec extends Specification {

    def locationConverter = new LocationConverter()

    def "convertToEntity should convert LocationDTO to Location"() {

        given: "a LocationDTO object with a nested UserDTO"
            UserDTO ownerDTO = new UserDTO(name: "name", email: "name@excample.com")
            LocationDTO locationDTO = new LocationDTO(
                name: "Location",
                address: "123 Main St, Springfield, IL, 62704",
                owner: ownerDTO
            )

        when: "convertToEntity is called"
            Location location = locationConverter.convertToEntity(locationDTO)

        then: "the Location object should have the same properties as LocationDTO"
            location.getName() == locationDTO.getName()
            location.getAddress() == locationDTO.getAddress()
            location.getOwner().getName() == locationDTO.getOwner().getName()
            location.getOwner().getEmail() == locationDTO.getOwner().getEmail()
    }

    def "test convertToDto converts Location to LocationDTO"() {

        given: "A Location object with an associated User"
            def user = new User(name: "name", email: "name@example.com")
            def location = new Location(
                name: "Location",
                address: "123 Main St, Springfield, IL, 62704",
                owner: user)

        when: "convertToDto is called"
            def locationDTO = locationConverter.convertToDto(location)

        then: "The resulting LocationDTO object should have the same properties, including owner"
            locationDTO.getName() == location.getName()
            locationDTO.getAddress() == location.getAddress()
            locationDTO.getOwner().getName() == location.getOwner().getName()
            locationDTO.getOwner().getEmail() == location.getOwner().getEmail()
    }
}