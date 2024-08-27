package com.klachkova.locationsystem.controllers

import com.klachkova.locationsystem.dto.LocationDTO
import com.klachkova.locationsystem.dto.UserDTO
import com.klachkova.locationsystem.modeles.AccessLevel
import com.klachkova.locationsystem.modeles.Location
import com.klachkova.locationsystem.modeles.User
import com.klachkova.locationsystem.services.LocationAccessService
import com.klachkova.locationsystem.services.LocationService
import com.klachkova.locationsystem.util.converters.LocationConverter
import com.klachkova.locationsystem.util.converters.UserConverter
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class LocationControllerSpec extends Specification {

    LocationService locationService = Mock()
    LocationConverter locationConverter = Mock()
    LocationAccessService locationAccessService = Mock()
    UserConverter userConverter = Mock()

    @Subject
    def locationController = new LocationController(
            locationService,
            locationConverter,
            locationAccessService,
            userConverter
    )

    def "test registerLocation should return CREATED status and LocationDTO"() {
        given:
        def locationDTO = new LocationDTO()
        def location = new Location()
        def registeredLocation = location.setId(1)

        and:
        locationConverter.convertToEntity(locationDTO) >> location
        locationService.registerLocation(location) >> registeredLocation
        locationConverter.convertToDto(location) >> locationDTO

        when:
        def response = locationController.registerLocation(locationDTO)

        then:
        response.statusCode == HttpStatus.CREATED
        response.body == locationDTO
    }

    def "test shareLocation should return OK status on successful sharing"() {
        given:
        def locationId = 1
        def userEmail = "name@example.com"
        def accessLevel = AccessLevel.READ_ONLY

        when:
        def response = locationController.shareLocation(locationId, userEmail, accessLevel)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "Location shared successfully"
    }

    def "test updateAccessLevel should return OK status on successful update"() {
        given:
        def locationId = 1
        def userEmail = "name@example.com"
        def accessLevel = AccessLevel.ADMIN

        when:
        def response = locationController.updateAccessLevel(locationId, userEmail, accessLevel)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "Access level updated successfully."
    }

    def "test getAllFriendUsers should return OK status and list of UserDTOs if there are friends"() {
        given:
        def locationId = 1
        def user1  = new User ()
        def user2 = new User()
        def userDTO1 = new UserDTO()
        def userDTO2 = new UserDTO()

        and:
        locationService.getFriendsToLocation(locationId) >> [user1, user2]
        userConverter.convertToDto(user1) >> userDTO1
        userConverter.convertToDto(user2) >> userDTO2

        when:
       def response = locationController.getAllFriendUsers(locationId)

        then:
        response.statusCode == HttpStatus.OK
        response.body.size() == 2
        response.body[0] == userDTO1
        response.body[1] == userDTO2
    }

    def "test getAllFriendUsers should return NO CONTENT and empty list of UserDTOs if there are no friends"() {
        given:
        def locationId = 1

        and:
        locationService.getFriendsToLocation(locationId) >> []

        when:
        def response = locationController.getAllFriendUsers(locationId)

        then:
        response.statusCode == HttpStatus.NO_CONTENT
        response.body.size() == 0
    }

}

