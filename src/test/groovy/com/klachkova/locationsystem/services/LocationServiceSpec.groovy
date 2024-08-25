package com.klachkova.locationsystem.services

import com.klachkova.locationsystem.modeles.Location
import com.klachkova.locationsystem.modeles.User
import com.klachkova.locationsystem.repositories.LocationRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
/*
class LocationServiceSpec extends Specification {

    @Autowired
    LocationRepository locationRepository = Mock()
    @Autowired
    LocationAccessService locationAccessService = Mock()
    @Autowired
    UserService userService = Mock()

    @Subject
    LocationService locationService = new LocationService(locationRepository, locationAccessService, userService)


    def "test registerLocation should register a location"() {

        given:
        def location = new Location(address: "123 Main St", owner: new User(email: "owner@example.com"))

        when:
        locationService.registerLocation(location)

        then:
        1 * userService.findByEmail("owner@example.com") >> location.owner
        1 * locationRepository.save(location)
    }

    def "test findByAddress should return location when found"() {

        given:
        def address = "123 Main St"
        def location = new Location(address: address)
        locationRepository.findByAddress(address) >> Optional.of(location)

        when:
        def result = locationService.findByAddress(address)

        then:
        result == location
    }

    def "test findByAddress should throw noSuchElementException when location not found"() {

        given:
        def address = "123 Main St"
        locationRepository.findByAddress(address) >> Optional.empty()

        when:
        locationService.findByAddress(address)

        then:
        def e = thrown(NoSuchElementException)
        e.message == "Location with address 123 Main St not found"
    }

    @Unroll

    def "test existsByAddress should return #expectedResult when email is #address"() {

        given:
        locationRepository.existsByAddress(address) >> expectedResult

        expect:
        locationService.existsByAddress(address) == expectedResult

        where:
        address        | expectedResult
        "123 True St"  | true
        "456 False St" | false
    }

    def "should check relevance of location and update owner"() {
        given:
        def location = new Location(address: "123 Main St", owner: new User(email: "owner@example.com"))
        def updatedOwner = new User(email: "owner@example.com")
        userService.findByEmail("owner@example.com") >> updatedOwner

        when:
        locationService.checkRelevanceLocation(location)

        then:
        location.owner == updatedOwner
    }

    def "should get all shared locations for a user"() {
        given:
        def user = new User(email: "user@example.com")
        def locations = [new Location(address: "123 Main St"), new Location(address: "456 Maple St")]
        locationAccessService.getAllSharedLocations(user) >> locations

        when:
        def result = locationService.getAllSharedLocations(user)

        then:
        result == locations
    }

    def "should get friends with access to location by locationId"() {
        given:
        def locationId = 1
        def users = [new User(email: "friend1@example.com"), new User(email: "friend2@example.com")]
        locationAccessService.getFriendsWithAccess(locationId) >> users

        when:
        def result = locationService.getFriendsWithAccessToLocation(locationId)

        then:
        result == users
    }
}*/