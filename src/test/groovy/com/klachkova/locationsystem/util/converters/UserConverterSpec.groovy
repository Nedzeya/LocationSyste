package com.klachkova.locationsystem.util.converters

import com.klachkova.locationsystem.dto.UserDTO
import com.klachkova.locationsystem.modeles.User
import org.modelmapper.ModelMapper
import spock.lang.Specification

class UserConverterSpec extends Specification {

    def modelMapper = new ModelMapper()
    def userConverter = new UserConverter(modelMapper)

    def "test convertToEntity converts UserDTO to User"() {
        given:
        def userDTO = new UserDTO()
        userDTO.setName("John Doe")
        userDTO.setEmail("john.doe@example.com")

        when:
        def user = userConverter.convertToEntity(userDTO)

        then:
        user.name == userDTO.getName()
        user.email == userDTO.getEmail()
    }

    def "test convertToDto converts User to UserDTO"() {
        given:
        def user = new User()
        user.setName("Jane Doe")
        user.setEmail("jane.doe@example.com")

        when:
        def userDTO = userConverter.convertToDto(user)

        then:
        userDTO.name == user.getName()
        userDTO.email == user.getEmail()
    }
}