package com.klachkova.locationsystem.dto

import spock.lang.Specification

class UserDTOSpec extends Specification {

    def "test UserDTO getters and setters"() {

        given:
            def userDTO = new UserDTO()
            def name = "name"
            def email = "name@example.com"

        when:
            userDTO.setName(name)
            userDTO.setEmail(email)

        then:
            userDTO.getName() == name
            userDTO.getEmail() == email
    }

    def "test UserDTO default constructor"() {

        given:
            def userDTO = new UserDTO()

        expect:
            userDTO.getName() == null
            userDTO.getEmail() == null
    }

    def "test UserDTO with valid values"() {

        given:
            def userDTO = new UserDTO(
                name: "name",
                email: "name@example.com"
            )

        expect:
            userDTO.getName() == "name"
            userDTO.getEmail() == "name@example.com"
    }

    def "test UserDTO with null values"() {

        given:
            def userDTO = new UserDTO(
                name: null,
                email: null
            )

        expect:
            userDTO.getName() == null
            userDTO.getEmail() == null
    }

    def "test UserDTO with empty values"() {

        given:
            def userDTO = new UserDTO(
                name: "",
                email: ""
            )

        expect:
            userDTO.getName() == ""
            userDTO.getEmail() == ""
    }
}