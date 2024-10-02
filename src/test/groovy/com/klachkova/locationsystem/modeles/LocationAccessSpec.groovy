package com.klachkova.locationsystem.modeles

import spock.lang.Specification
import spock.lang.Unroll
import javax.validation.Validation
import javax.validation.Validator

class LocationAccessSpec extends Specification {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator()

    def "test LocationAccess creation with valid data"() {

        given:
            def user = new User(name: "John Doe", email: "john.doe@example.com")
            def location = new Location(id: 1, name: "Library", address: "123 Main St, Springfield, IL, 62704", owner: user)
            def accessLevel = AccessLevel.READ_ONLY
            def locationAccess = new LocationAccess(user, location, accessLevel)

        when:
            def violations = validator.validate(locationAccess)

        then:
            violations.isEmpty()
            locationAccess.getUser() == user
            locationAccess.getLocation() == location
            locationAccess.getAccessLevel() == accessLevel
    }

    @Unroll
    def "test LocationAccess creation with missing #field"() {

        given:
            def user = field == 'user' ? null : new User(name: "John Doe", email: "john.doe@example.com")
            def location = field == 'location' ? null : new Location(id: 1, name: "Library", address: "123 Main St, Springfield, IL, 62704", owner: user)
            def accessLevel = field == 'accessLevel' ? null : AccessLevel.READ_ONLY
            def locationAccess = new LocationAccess(user, location, accessLevel)

        when:
            def violations = validator.validate(locationAccess)

        then:
            def messages = violations.collect { it.message }.toSet()
            messages.contains(expectedError)

        where:
            field         | expectedError
            'user'        | "User should not be empty"
            'location'    | "Location should not be empty"
            'accessLevel' | "Access level must not be null"
    }

    def "test LocationAccess creation with null access level"() {

        given:
            def user = new User(name: "John Doe", email: "john.doe@example.com")
            def location = new Location(id: 1, name: "Library", address: "123 Main St, Springfield, IL, 62704", owner: user)
            def locationAccess = new LocationAccess(user, location, null)

        when:
            def violations = validator.validate(locationAccess)

        then:
            violations.first().message == "Access level must not be null"
    }

    def "test LocationAccess creation with all fields set correctly"() {

        given:
            def user = new User(name: "John Doe", email: "john.doe@example.com")
            def location = new Location(id: 1, name: "Library", address: "123 Main St, Springfield, IL, 62704", owner: user)
            def accessLevel = AccessLevel.READ_ONLY
            def locationAccess = new LocationAccess(user, location, accessLevel)

        when:
            def violations = validator.validate(locationAccess)

        then:
            violations.isEmpty()
            locationAccess.getUser() == user
            locationAccess.getLocation() == location
            locationAccess.getAccessLevel() == accessLevel
    }

    def "test getters and setters for LocationAccess"() {

        given:
            def user = new User(name: "John Doe", email: "john.doe@example.com")
            def location = new Location(id: 1, name: "Library", address: "123 Main St, Springfield, IL, 62704", owner: user)
            def accessLevel = AccessLevel.READ_ONLY
            def locationAccess = new LocationAccess(user: user, location: location, accessLevel: accessLevel)
            locationAccess.setId(1)

        when:
            def id = locationAccess.getId()
            def retrievedUser = locationAccess.getUser()
            def retrievedLocation = locationAccess.getLocation()
            def retrievedAccessLevel = locationAccess.getAccessLevel()

        then:
            id == 1
            retrievedUser == user
            retrievedLocation == location
            retrievedAccessLevel == accessLevel
    }

    def "test setId method for LocationAccess"() {

        given:
            def locationAccess = new LocationAccess()

        when:
            locationAccess.setId(2)

        then:
            locationAccess.getId() == 2
    }

    def "test setUser method for LocationAccess"() {

        given:
            def locationAccess = new LocationAccess()
            def user = new User(name: "John Doe", email: "john.doe@example.com")

        when:
            locationAccess.setUser(user)

        then:
            locationAccess.getUser() == user
    }

    def "test setLocation method for LocationAccess"() {

        given:
            def locationAccess = new LocationAccess()
            def location = new Location(id: 1, name: "Library", address: "123 Main St, Springfield, IL, 62704")

        when:
            locationAccess.setLocation(location)

        then:
            locationAccess.getLocation() == location
    }

    def "test setAccessLevel method for LocationAccess"() {

        given:
            def locationAccess = new LocationAccess()
            def accessLevel = AccessLevel.ADMIN

        when:
            locationAccess.setAccessLevel(accessLevel)

        then:
            locationAccess.getAccessLevel() == accessLevel
    }
}
