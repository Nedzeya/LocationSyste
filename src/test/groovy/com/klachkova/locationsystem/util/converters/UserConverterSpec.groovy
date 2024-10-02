package com.klachkova.locationsystem.util.converters

import com.klachkova.locationsystem.dto.UserDTO
import com.klachkova.locationsystem.modeles.User
import spock.lang.Specification

class UserConverterSpec extends Specification {

    def userConverter = new UserConverter()

    def "test convertToEntity converts UserDTO to User"() {

        given:
            def userDTO = new UserDTO()
            userDTO.setName("name")
            userDTO.setEmail("name@example.com")

        when:
            def user = userConverter.convertToEntity(userDTO)

        then:
            user.getName() == userDTO.getName()
            user.getEmail() == userDTO.getEmail()
    }

    def "test convertToDto converts User to UserDTO"() {

        given:
            def user = new User()
            user.setName("name")
            user.setEmail("name@example.com")

        when:
            def userDTO = userConverter.convertToDto(user)

        then:
            userDTO.getName() == user.getName()
            userDTO.getEmail() == user.getEmail()
    }
}