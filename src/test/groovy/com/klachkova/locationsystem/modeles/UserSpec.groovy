package com.klachkova.locationsystem.modeles

import spock.lang.Specification
import spock.lang.Unroll
import javax.validation.Validation
import javax.validation.Validator

class UserSpec extends Specification {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator()

    def " test User creation with valid name and email"() {
        given:
        def name = "Ivan Ivanov"
        def email = "ivan@example.com"

        when:
        def user = new User(name, email)

        then:
        user.getName() == name
        user.getEmail()== email
    }
    @Unroll
    def "test User should return validation errors for name '#name'"() {
        given:
        def email = "ivan@example.com"
        def user = new User(name, email)

        when:
        def violations = validator.validate(user)

        then:
        def messages = violations.collect { it.message }.toSet()
        messages.contains("Name should not be empty")
        messages.contains("Name should be between 2 and 30 characters")

        where:
        name | _
        ""   | _
    }

    @Unroll
    def "test User creation with invalid length name '#name' should result in validation error"() {
        given:
        def email = "ivan@example.com"
        def user = new User(name, email)

        when:
        def violations = validator.validate(user)

        then:
        violations.size() == 1
        violations.first().message == expectedError

        where:
        name                              | expectedError
        "A"                               | "Name should be between 2 and 30 characters"
        "John Jacob Verylongname Schmidt" | "Name should be between 2 and 30 characters"
    }

    @Unroll
    def "test User creation with invalid email '#email' should result in validation error"() {
        given:
        def name = "John Doe"
        def user = new User(name, email)

        when:
        def violations = validator.validate(user)

        then:
        violations.size() == 1
        violations.first().message == expectedError

        where:
        email           | expectedError
        ""              | "Email should not be empty"
        "invalid-email" | "Email should be valid"
        "john.doe.com"  | "Email should be valid"
    }

    def "test User creation with empty locations list"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")

        when:
        def locations = user.getLocations()

        then:
        locations == null
    }

    def "test User creation with all fields and no validation errors"() {
        given:
        def name = "John Doe"
        def email = "john.doe@example.com"
        def location = new Location(name: "Home", address: "123 Main St")
        def user = new User(name: name, email: email)
        user.setId(1)
        user.locations = [location]

        when:
        def violations = validator.validate(user)

        then:
        violations.isEmpty()
        user.name == name
        user.email == email
        user.id == 1
        user.locations.size() == 1
        user.locations.first().name == "Home"
    }

    def "test set and get ID"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")

        when:
        user.setId(10)

        then:
        user.getId() == 10
    }

    def "test get and set Name"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")

        when:
        user.setName("New Name")

        then:
        user.getName() == "New Name"
    }

    def "test get and set Email"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")

        when:
        user.setEmail("new.email@example.com")

        then:
        user.getEmail() == "new.email@example.com"
    }

    def "test get and set Locations"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")
        def locations = [new Location(name: "Home", address: "123 Main St")]

        when:
        user.setLocations(locations)

        then:
        user.getLocations() == locations
    }
}