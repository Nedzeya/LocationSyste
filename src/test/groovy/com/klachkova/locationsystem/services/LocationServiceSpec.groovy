package com.klachkova.locationsystem.services

import com.klachkova.locationsystem.modeles.Location
import com.klachkova.locationsystem.modeles.User
import com.klachkova.locationsystem.repositories.LocationRepository
import com.klachkova.locationsystem.util.NotCreatedException
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class LocationServiceSpec extends Specification {

    LocationRepository locationRepository = Mock()
    LocationAccessService locationAccessService = Mock()
    UserService userService = Mock()

    @Subject
    LocationService locationService = new LocationService(locationRepository, locationAccessService, userService)


    def "test registerLocation saves location"() {

        given:
        def email = "name1@example.com"
        def user = new User(email: email)
        def locationAddress = "123 Main St, Springfield, IL, 62704"
        def location = new Location(address: locationAddress, owner: user)

        and:
        userService.existsByEmail(email) >> true
        locationRepository.existsByAddress(location.getAddress()) >> false

        when:
        locationService.registerLocation(location)

        then:
        1 * locationRepository.save(location)
    }

    def "test registerLocation should throw NotCreatedException if user does not exist"() {
        given:
        def email = "name1@example.com"
        def user = new User(email: email)
        def location = new Location(owner: user)

        and:
        userService.existsByEmail(email) >> false

        when:
        locationService.registerLocation(location)

        then:
        thrown(NotCreatedException)
    }

    def "test registerLocation should throw NotCreatedException if location address already exists"() {
        given:
        def email = "name1@example.com"
        def owner = new User(email: email)
        def locationAddress = "123 Main St, Springfield, IL, 62704"
        def location = new Location(address: locationAddress, owner: owner)

        and:
        userService.existsByEmail(email) >> true
        locationRepository.existsByAddress(locationAddress) >> true

        when:
        locationService.registerLocation(location)

        then:
        thrown(NotCreatedException)
    }


    def "test findByAddress should return the location when found"() {

        given:
        def locationAddress = "123 Main St, Springfield, IL, 62704"
        def location = new Location(address: locationAddress)

        and:
        locationRepository.findByAddress(locationAddress) >> Optional.of(location)

        when:
        def result = locationService.findByAddress(locationAddress)

        then:
        result == location
    }

    def "test findByAddress should throw NoSuchElementException when location not found"() {

        given:
        def locationAddress = "123 Main St, Springfield, IL, 62704"

        and:
        locationRepository.findByAddress(locationAddress) >> Optional.empty()

        when:
        locationService.findByAddress(locationAddress)

        then:
        thrown(NoSuchElementException)
    }

    def "test findById should return the location when found"() {
        given:
        def locationId = 1
        def location = new Location(id: locationId)

        and:
        locationRepository.findById(locationId) >> Optional.of(location)

        when:
        def result = locationService.findById(locationId)

        then:
        result == location
    }

    def "test findById should throw NoSuchElementException when location not found"() {
        given:
        def locationId = 1

        and:
        locationRepository.findById(locationId) >> Optional.empty()

        when:
        locationService.findById(locationId)

        then:
        thrown(NoSuchElementException)
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

    def "test getAllSharedLocations should return shared locations for user"() {
        given:
        def user = new User()
        def location1 = new Location()
        def location2 = new Location()

        and:
        locationAccessService.getAllSharedLocations(user) >> [location1, location2]

        when:
        def result = locationService.getAllSharedLocations(user)

        then:
        result.size() == 2
        result.contains(location1)
        result.contains(location2)
    }


    def "test getFriendsToLocation should return users with access to the location"() {
        given:
        def locationId = 1
        def user1 = new User()
        def user2 = new User()

        and:
        locationAccessService.getFriends(locationId) >> [user1, user2]

        when:
        def result = locationService.getFriendsToLocation(locationId)

        then:
        result.size() == 2
        result.contains(user1)
        result.contains(user2)
    }
}