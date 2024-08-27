package com.klachkova.locationsystem.dto

import spock.lang.Specification

import javax.validation.Validation
import javax.validation.Validator

class UserDTOSpec extends Specification{

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator()

    def "test UserDTO should be valid with correct values"() {
        given:
        def userDTO = new UserDTO(
                name: "Valid Name",
                email: "validEmail@example.com"
        )

        when:
        def violations = validator.validate(userDTO)

        then:
        violations.isEmpty()
    }

    def "test UserDTO should be invalid when name is blank"() {
        given:
        def userDTO = new UserDTO(
                name: "",
                email: "validEmail@example.com"
        )

        when:
        def violations = validator.validate(userDTO)

        then:
        !violations.isEmpty()
        violations.message.contains("Name should not be empty")
    }

    def "test UserDTO should be invalid when name is too short"() {
        given:
        def userDTO = new UserDTO(
                name: "A",
                email: "validEmail@example.com"
        )

        when:
        def violations = validator.validate(userDTO)

        then:
        !violations.isEmpty()
        violations*.message.contains("Name should be between 2 and 30 characters")
    }

    def "test UserDTO should be invalid when name is too long"() {
        given:
        def longName = new String(new char[31]).replace('\0', 'A')  // Generates a string with 31 'A' characters
        def userDTO = new UserDTO(
                name: longName,
                email: "valid.email@example.com"
        )

        when:
        def violations = validator.validate(userDTO)

        then:
        !violations.isEmpty()
        violations*.message.contains("Name should be between 2 and 30 characters")
    }

    def "test UserDTO should be invalid when email is blank"() {
        given:
        def userDTO = new UserDTO(
                name: "Valid Name",
                email: ""
        )

        when:
        def violations = validator.validate(userDTO)

        then:
        !violations.isEmpty()
        violations*.message.contains("Email should not be empty")
    }

    def "test UserDTO should be invalid when email format is incorrect"() {
        given:
        def userDTO = new UserDTO(
                name: "Valid Name",
                email: "invalid-email"
        )

        when:
        def violations = validator.validate(userDTO)

        then:
        !violations.isEmpty()
        violations*.message.contains("Email should be valid")
    }
}
