package com.klachkova.locationsystem.dto

import spock.lang.Specification

class LocationDTOSpec extends Specification {

    def "test LocationDTO getters and setters"() {

        given:
            def locationDTO = new LocationDTO()
            def name = "Location"
            def address = "123 Main St, Springfield, IL, 62704"
            def owner = new UserDTO(name: "name", email: "name@example.com")

        when:
            locationDTO.setName(name)
            locationDTO.setAddress(address)
            locationDTO.setOwner(owner)

        then:
            locationDTO.getName() == name
            locationDTO.getAddress() == address
            locationDTO.getOwner() == owner
    }

    def "test LocationDTO default constructor"() {

        given:
            def locationDTO = new LocationDTO()

        expect:
            locationDTO.getName() == null
            locationDTO.getAddress() == null
            locationDTO.getOwner() == null
    }

    def "test LocationDTO with valid values"() {

        given:
            def userDTO = new UserDTO(name: "name", email: "name@example.com")
            def locationDTO = new LocationDTO(
                name: "Location",
                address: "123 Valid St, Springfield, IL, 62704",
                owner: userDTO
            )

        expect:
            locationDTO.getName() == "Location"
            locationDTO.getAddress() == "123 Valid St, Springfield, IL, 62704"
            locationDTO.getOwner() == userDTO
    }

    def "test LocationDTO with null values"() {

        given:
            def locationDTO = new LocationDTO(
                name: null,
                address: null,
                owner: null
            )

        expect:
            locationDTO.getName() == null
            locationDTO.getAddress() == null
            locationDTO.getOwner() == null
    }

    def "test LocationDTO with empty values"() {

        given:
            def userDTO = new UserDTO(name: "", email: "")
            def locationDTO = new LocationDTO(
                name: "",
                address: "",
                owner: userDTO
            )

        expect:
            locationDTO.getName() == ""
            locationDTO.getAddress() == ""
            locationDTO.getOwner() == userDTO
    }
}
