package com.klachkova.locationsystem.modeles

import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.Validation
import javax.validation.Validator

class LocationSpec extends Specification {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator()

    def "test Location creation with valid fields"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")
        def location = new Location(1, "Library", "123 Main St, Springfield, IL, 62704", user)

        when:
        def violations = validator.validate(location)

        then:
        violations.isEmpty()
        location.getName() == "Library"
        location.getAddress() == "123 Main St, Springfield, IL, 62704"
        location.getOwner() == user
        location.getId() == 1
    }

    @Unroll
    def "test Location should return validation errors for name '#name'"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")
        def location = new Location(1, name, "123 Main St, Springfield, IL, 62704", user)

        when:
        def violations = validator.validate(location)

        then:
        def messages = violations.collect { it.message }.toSet()
        messages.contains("Name should not be empty")
        messages.contains("Name should be between 2 and 30 characters")

        where:
        name | _
        ""   | _
    }

    @Unroll
    def "test Location creation with invalid length name '#name'"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")
        def location = new Location(1, name, "123 Main St, Springfield, IL, 62704", user)

        when:
        def violations = validator.validate(location)

        then:
        violations.size() == 1
        violations.first().message == expectedError

        where:
        name                                                                | expectedError
        "A"                                                                 | "Name should be between 2 and 30 characters"
        "This is a very long location name that exceeds the maximum length" | "Name should be between 2 and 30 characters"
    }

    @Unroll
    def "test Location creation with invalid address '#address'"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")
        def location = new Location(1, "Library", address, user)

        when:
        def violations = validator.validate(location)

        then:
        def messages = violations.collect { it.message }.toSet()
        messages.contains("Address must be in US format (ex.: '123 Main St, Springfield, IL, 62704')")

        where:
        address                    | _
        ""                         | _
        "Invalid Address"          | _
        "123 Main St, Springfield" | _
    }

     def "test Location creation with missing owner"() {
         given:
         def location = new Location(1, "Library", "123 Main St, Springfield, IL, 62704", null)

         when:
         def violations = validator.validate(location)

         then:
         def messages = violations.collect { it.message }.toSet()
         messages.contains("Owner should not be empty")

     }

    def "test Location creation with empty shared users list"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")
        def location = new Location(1, "Library", "123 Main St, Springfield, IL, 62704", user)
        location.setSharedUsers([])

        when:
        def sharedUsers = location.getSharedUsers()

        then:
        sharedUsers.isEmpty()
    }

    def "test Set and get Location ID"() {
        given:
        def location = new Location()

        when:
        location.setId(10)

        then:
        location.getId() == 10
    }

    def "test Set and get Location name"() {
        given:
        def location = new Location()

        when:
        location.setName("Library")

        then:
        location.getName() == "Library"
    }

    def "test Set and get Location address"() {
        given:
        def location = new Location()

        when:
        location.setAddress("123 Main St, Springfield, IL, 62704")

        then:
        location.getAddress() == "123 Main St, Springfield, IL, 62704"
    }

    def "test Set and get Location owner"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")
        def location = new Location()

        when:
        location.setOwner(user)

        then:
        location.getOwner() == user
    }

    def "test Set and get Location shared users"() {
        given:
        def user = new User(name: "John Doe", email: "john.doe@example.com")
        def location = new Location()
        def sharedUsers = [user]

        when:
        location.setSharedUsers(sharedUsers)

        then:
        location.getSharedUsers() == sharedUsers
    }
}
