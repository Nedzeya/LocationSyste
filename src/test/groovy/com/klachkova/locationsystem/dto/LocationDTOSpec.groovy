package com.klachkova.locationsystem.dto

import spock.lang.Specification
import javax.validation.Validation
import javax.validation.Validator

class LocationDTOSpec extends Specification {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator()

    def "test LocationDTO should be valid with correct values"() {
        given:
        def locationDTO = new LocationDTO(
                name: "Valid Name",
                address: "123 Main St, Springfield, IL, 62704",
                owner: new UserDTO()  // Assuming UserDTO is a valid class for this example
        )

        when:
        def violations = validator.validate(locationDTO)

        then:
        violations.isEmpty()
    }

    def "test LocationDTO should be invalid when name is blank"() {
        given:
        def locationDTO = new LocationDTO(
                name: "",
                address: "123 Main St, Springfield, IL, 62704",
                owner: new UserDTO()
        )

        when:
        def violations = validator.validate(locationDTO)

        then:
        !violations.isEmpty()
        violations*.message.contains("Name should not be empty")
    }

    def "test LocationDTO should be invalid when name is too short"() {
        given:
        def locationDTO = new LocationDTO(
                name: "A",
                address: "123 Main St, Springfield, IL, 62704",
                owner: new UserDTO()
        )

        when:
        def violations = validator.validate(locationDTO)

        then:
        !violations.isEmpty()
        violations*.message.contains("Name should be between 2 and 30 characters")
    }

    def "test LocationDTO should be invalid when name is too long"() {
        given:
        def longName = new String(new char[31]).replace('\0', 'A')  // Generates a string with 31 'A' characters
        def locationDTO = new LocationDTO(
                name: longName,
                address: "123 Main St, Springfield, IL, 62704",
                owner: new UserDTO()
        )

        when:
        def violations = validator.validate(locationDTO)

        then:
        !violations.isEmpty()
        violations*.message.contains("Name should be between 2 and 30 characters")
    }

    def "test LocationDTO should be invalid when address is blank"() {
        given:
        def locationDTO = new LocationDTO(
                name: "Valid Name",
                address: "",
                owner: new UserDTO()
        )

        when:
        def violations = validator.validate(locationDTO)

        then:
        !violations.isEmpty()
        violations*.message.contains("Address should not be empty")
    }

    def "test LocationDTO should be invalid when address format is incorrect"() {
        given:
        def locationDTO = new LocationDTO(
                name: "Valid Name",
                address: "Invalid Address",
                owner: new UserDTO()
        )

        when:
        def violations = validator.validate(locationDTO)

        then:
        !violations.isEmpty()
        violations*.message.contains("Address must be in US format (ex.: '123 Main St, Springfield, IL, 62704')")
    }

    def "test LocationDTO should be invalid when owner is null"() {
        given:
        def locationDTO = new LocationDTO(
                name: "Valid Name",
                address: "123 Main St, Springfield, IL, 62704",
                owner: null
        )

        when:
        def violations = validator.validate(locationDTO)

        then:
        !violations.isEmpty()
        violations*.message.contains("The field must not be empty")
    }
}
