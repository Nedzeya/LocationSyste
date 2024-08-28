package com.klachkova.locationsystem.services

import com.klachkova.locationsystem.modeles.AccessLevel
import com.klachkova.locationsystem.modeles.Location
import com.klachkova.locationsystem.modeles.LocationAccess
import com.klachkova.locationsystem.modeles.User
import com.klachkova.locationsystem.repositories.LocationAccessRepository
import com.klachkova.locationsystem.util.exceptions.NotFoundException
import com.klachkova.locationsystem.util.exceptions.PermissionDeniedException
import com.klachkova.locationsystem.util.exceptions.ValidationException
import spock.lang.Specification
import spock.lang.Subject

import javax.validation.ConstraintViolation
import javax.validation.Validator

class LocationAccessServiceSpec extends Specification {

    LocationAccessRepository locationAccessRepository = Mock(LocationAccessRepository)
    UserService userService = Mock(UserService)
    LocationService locationService = Mock(LocationService)
    Validator validator = Mock()

    @Subject
    LocationAccessService locationAccessService = new LocationAccessService(locationAccessRepository, userService, locationService, validator)

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
        def locationId =1
        def location = new Location(id: locationId)
        def accessLevel = AccessLevel.READ_ONLY

        when:
        validator.validate(_ as LocationAccess) >> []
        locationAccessService.shareLocation(locationId, friendEmail, accessLevel)

        then:
        1 * userService.findByEmail(friendEmail) >> friend
        1 * locationService.findById(locationId) >> location
        1 * locationAccessRepository.save(_ as LocationAccess)
    }

    def "test updateLocationAccessByAccessLevel updates access level"() {
        given:
        def email = "name@example.com"
        def friend = new User(email: email)
        def locationId =1
        def location = new Location(id: locationId)
        def locationAccess = new LocationAccess(user: friend, location: location, accessLevel: AccessLevel.READ_ONLY)

        and:
        validator.validate(_ as LocationAccess) >> []
        userService.findByEmail(email) >> friend
        locationService.findById(locationId) >> location
        locationAccessRepository.findByLocationAndUser(location, friend) >> Optional.of(locationAccess)

        when:
        locationAccessService.updateLocationAccessByAccessLevel(locationId, email, AccessLevel.ADMIN)

        then:
        locationAccess.accessLevel == AccessLevel.ADMIN
        1 * locationAccessRepository.save(locationAccess)
    }

    def "test updateLocationAccessByAccessLevel throws NotFoundException if no LocationAccess found"() {
        given:
        def email = "name@example.com"
        def friend = new User(email: email)
        def locationId =1
        def location = new Location(id:locationId)

        and:
        userService.findByEmail(email) >> friend
        locationService.findById(locationId) >> location
        locationAccessRepository.findByLocationAndUser(location, friend) >> Optional.empty()

        when:
        locationAccessService.updateLocationAccessByAccessLevel(locationId, email, AccessLevel.ADMIN)

        then:
        thrown(NotFoundException)
    }

    def "test getFriends returns list of users with access to location"() {
        given:
        def friend1 = new User()
        def friend2 = new User()
        def locationId =1
        def location = new Location(id:locationId)
        def access1 = new LocationAccess(user: friend1, location: location)
        def access2 = new LocationAccess(user: friend2, location: location)

        and:
        locationAccessRepository.findByLocationId(locationId) >> [access1, access2]

        when:
        def result = locationAccessService.getFriends(locationId)

        then:
        result.size() == 2
        result.contains(friend1)
        result.contains(friend2)
    }

    def "test addFriendToLocation adds a friend if user has ADMIN access"() {
        given:
        def userId =1
        def user = new User(id: userId)
        def friendEmail = "name2@example.com"
        def friend = new User(email: friendEmail)
        def locationAddress = "123 Main St, Springfield, IL, 62704"
        def location = new Location(address: locationAddress)
        def adminAccess = new LocationAccess(user: user, location: location, accessLevel: AccessLevel.ADMIN)

        and:
        validator.validate(_ as LocationAccess) >> []
        userService.findById(userId) >> user
        userService.findByEmail(friendEmail) >> friend
        locationService.findByAddress(locationAddress) >> location
        locationAccessRepository.findByLocationAndUser(location, user) >> Optional.of(adminAccess)

        when:
        locationAccessService.addFriendToLocation(userId, friendEmail, locationAddress, AccessLevel.READ_ONLY)

        then:
        1 * locationAccessRepository.save(_ as LocationAccess)
    }

    def "test addFriendToLocation throws PermissionDeniedException if user does not have ADMIN access"() {
        given:
        def user = new User(id: 1)
        def friendEmail = "name2@example.com"
        def friend = new User(email: friendEmail)
        def locationAddress = "123 Main St, Springfield, IL, 62704"
        def location = new Location(address: locationAddress)
        def readAccess = new LocationAccess(user: user, location: location, accessLevel: AccessLevel.READ_ONLY)

        and:
        userService.findById(1) >> user
        userService.findByEmail(friendEmail) >> friend
        locationService.findByAddress(locationAddress) >> location
        locationAccessRepository.findByLocationAndUser(location, user) >> Optional.of(readAccess)

        when:
        locationAccessService.addFriendToLocation(1, friendEmail, locationAddress, AccessLevel.READ_ONLY)

        then:
        thrown(PermissionDeniedException)
    }

    def "test validateLocationAccess throws ValidationException for invalid LocationAccess"() {
        given:
        def invalidLocationAccess = new LocationAccess() // Assuming this LocationAccess instance is invalid
        def violation = Mock(ConstraintViolation)
        violation.getMessage() >> "Invalid locationAccess"

        and:
        validator.validate(invalidLocationAccess) >> [violation]

        when:
        locationAccessService.validateLocationAccess(invalidLocationAccess)

        then:
        thrown(ValidationException)
    }
}