package com.klachkova.locationsystem.services

import com.klachkova.locationsystem.modeles.AccessLevel
import com.klachkova.locationsystem.modeles.Location
import com.klachkova.locationsystem.modeles.LocationAccess
import com.klachkova.locationsystem.modeles.User
import com.klachkova.locationsystem.repositories.LocationAccessRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Subject

class LocationAccessServiceSpec extends Specification {

    @Autowired
    LocationAccessRepository locationAccessRepository = Mock(LocationAccessRepository)

    @Autowired
    UserService userService = Mock(UserService)

    @Autowired
    LocationService locationService = Mock(LocationService)

    @Subject
    LocationAccessService locationAccessService = new LocationAccessService(locationAccessRepository, userService, locationService)

    def "test getAllSharedLocations returns shared locations for a user"() {
        given:
        def user = new User()
        def location1 = new Location()
        def location2 = new Location()
        def locationAccess1 = new LocationAccess(user: user, location: location1)
        def locationAccess2 = new LocationAccess(user: user, location: location2)

        and:
        locationAccessRepository.findByUser(user) >> [locationAccess1, locationAccess2]

        when:
        def result = locationAccessService.getAllSharedLocations(user)

        then:
        result.size() == 2
        result.contains(location1)
        result.contains(location2)
    }

    def "test shareLocation saves new LocationAccess"() {
        given:
        def friendEmail = "name@example.com"
        def friend = new User(email: friendEmail)
        def location = new Location(id: 1)
        def accessLevel = AccessLevel.READ_ONLY

        when:
        locationAccessService.shareLocation(1, friendEmail, accessLevel)

        then:
        1 * locationAccessRepository.save(_ as LocationAccess)
    }

    def "test updateLocationAccessByAccessLevel updates access level"() {
        given:
        def email = "name@example.com"
        def friend = new User(email: email)
        def location = new Location(id: 1)
        def locationAccess = new LocationAccess(user: friend, location: location, accessLevel: AccessLevel.READ_ONLY)

        and:
        userService.findByEmail(email) >> friend
        locationService.findById(1) >> location
        locationAccessRepository.findByLocationAndUser(location, friend) >> Optional.of(locationAccess)

        when:
        locationAccessService.updateLocationAccessByAccessLevel(1, email, AccessLevel.ADMIN)

        then:
        locationAccess.accessLevel == AccessLevel.ADMIN
        1 * locationAccessRepository.save(locationAccess)
    }

    def "test updateLocationAccessByAccessLevel throws NoSuchElementException if no LocationAccess found"() {
        given:
        def email = "name@example.com"
        def friend = new User(id: 1, email: email)
        def location = new Location(id: 1)

        and:
        userService.findByEmail(email) >> friend
        locationService.findById(location.id) >> location
        locationAccessRepository.findByLocationAndUser(location, friend) >> Optional.empty()

        when:
        locationAccessService.updateLocationAccessByAccessLevel(1, email, AccessLevel.ADMIN)

        then:
        thrown(NoSuchElementException)
    }

    def "test getFriends returns list of users with access to location"() {
        given:
        def friend1 = new User()
        def friend2 = new User()
        def location = new Location(id: 1)
        def access1 = new LocationAccess(user: friend1, location: location)
        def access2 = new LocationAccess(user: friend2, location: location)

        and:
        locationAccessRepository.findByLocationId(1) >> [access1, access2]

        when:
        def result = locationAccessService.getFriends(1)

        then:
        result.size() == 2
        result.contains(friend1)
        result.contains(friend2)
    }

    def "test addFriendToLocation adds a friend if user has ADMIN access"() {
        given:
        def user = new User(id: 1)
        def friendEmail = "name2@example.com"
        def friend = new User(email: friendEmail)
        def locationAddress = "123 Main St, Springfield, IL, 62704"
        def location = new Location(address: locationAddress)
        def adminAccess = new LocationAccess(user: user, location: location, accessLevel: AccessLevel.ADMIN)

        and:
        userService.findById(1) >> user
        userService.findByEmail(friendEmail) >> friend
        locationService.findByAddress(location.address) >> location
        locationAccessRepository.findByLocationAndUser(location, user) >> Optional.of(adminAccess)

        when:
        locationAccessService.addFriendToLocation(1, friendEmail, locationAddress, AccessLevel.READ_ONLY)

        then:
        1 * locationAccessRepository.save(_ as LocationAccess)
    }

    def "test addFriendToLocation throws SecurityException if user does not have ADMIN access"() {
        given:
        def user = new User(id: 1)
        def friendEmail = "name2@example.com"
        def friend= new User(email: friendEmail)
        def locationAddress = "123 Main St, Springfield, IL, 62704"
        def location = new Location(address: locationAddress)
        def readAccess = new LocationAccess(user: user, location: location, accessLevel: AccessLevel.READ_ONLY)

        and:
        userService.findById(1) >> user
        userService.findByEmail(friendEmail) >> friend
        locationService.findByAddress(location.address) >> location
        locationAccessRepository.findByLocationAndUser(location, user) >> Optional.of(readAccess)

        when:
        locationAccessService.addFriendToLocation(1, friendEmail, locationAddress, AccessLevel.READ_ONLY)

        then:
        thrown(SecurityException)
    }
}
