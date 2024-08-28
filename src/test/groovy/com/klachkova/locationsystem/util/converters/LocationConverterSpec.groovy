package com.klachkova.locationsystem.util.converters

import com.klachkova.locationsystem.dto.LocationDTO
import com.klachkova.locationsystem.dto.UserDTO
import com.klachkova.locationsystem.modeles.Location
import com.klachkova.locationsystem.modeles.User
import org.modelmapper.ModelMapper
import spock.lang.Specification

class LocationConverterSpec extends Specification {

    def modelMapper = new ModelMapper()
    def locationConverter = new LocationConverter(modelMapper)

    def "test convertToEntity converts LocationDTO to Location"() {
        given: "A LocationDTO object with an associated UserDTO"
        def userDTO = new UserDTO(name: "John Doe", email: "john.doe@example.com")
        def locationDTO = new LocationDTO(name: "Office", address: "123 Main St, Springfield, IL, 62704", owner: userDTO)

        when: "convertToEntity is called"
        def location = locationConverter.convertToEntity(locationDTO)

        then: "The resulting Location object should have the same properties, including owner"
        location.name == locationDTO.name
        location.address == locationDTO.address
        location.owner.name == locationDTO.owner.name
        location.owner.email == locationDTO.owner.email
    }

    def "test convertToDto converts Location to LocationDTO"() {
        given: "A Location object with an associated User"
        def user = new User(name: "Jane Doe", email: "jane.doe@example.com")
        def location = new Location(name: "Home", address: "456 Oak St, Springfield, IL, 62704", owner: user)

        when: "convertToDto is called"
        def locationDTO = locationConverter.convertToDto(location)

        then: "The resulting LocationDTO object should have the same properties, including owner"
        locationDTO.name == location.name
        locationDTO.address == location.address
        locationDTO.owner.name == location.owner.name
        locationDTO.owner.email == location.owner.email
    }
}