package com.klachkova.locationsystem.controllers

import com.klachkova.locationsystem.dto.UserDTO
import com.klachkova.locationsystem.dto.LocationDTO
import com.klachkova.locationsystem.modeles.AccessLevel
import com.klachkova.locationsystem.modeles.Location
import com.klachkova.locationsystem.modeles.User
import com.klachkova.locationsystem.services.LocationAccessService
import com.klachkova.locationsystem.services.LocationService
import com.klachkova.locationsystem.services.UserService
import com.klachkova.locationsystem.util.converters.LocationConverter
import com.klachkova.locationsystem.util.converters.UserConverter
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class UserControllerSpec extends Specification {
    UserService userService = Mock()
    UserConverter userConverter = Mock()
    LocationService locationService = Mock()
    LocationConverter locationConverter = Mock()
    LocationAccessService locationAccessService = Mock()

    @Subject
    def userController = new UserController(
            userService,
            userConverter,
            locationService,
            locationConverter,
            locationAccessService)

    def "test registerUser should create a user and return CREATED status and UserDTO"() {
        given:
        def userDTO = new UserDTO()
        def user = new User()
        def registeredUser = user.setId(1)

        and:
        userConverter.convertToEntity(userDTO) >> user
        userService.registerUser(user) >> registeredUser
        userConverter.convertToDto(registeredUser) >> userDTO

        when:
        def response = userController.registerUser(userDTO)

        then:
        response.statusCode == HttpStatus.CREATED
        response.body == userDTO
    }

    def "test getAvailableLocations returns correct response"() {
        given:
        def userId = 1
        def location1 = new Location()
        def location2 = new Location()
        def location3 = new Location()

        def ownLocations = [location1, location2]
        def sharedLocations = [location3]

        def locationDTO1 = new LocationDTO()
        def locationDTO2 = new LocationDTO()
        def locationDTO3 = new LocationDTO()

        and:
        locationService.getAvailableLocations(userId) >> [ownLocations, sharedLocations]
        locationConverter.convertToDto(location1) >> locationDTO1
        locationConverter.convertToDto(location2) >> locationDTO2
        locationConverter.convertToDto(location3) >> locationDTO3

        when:
        def response = userController.getAvailableLocations(userId)

        then:
        response.statusCode == HttpStatus.OK
        response.body.size() == 2
        response.body[0] == [locationDTO1, locationDTO2]
        response.body[1] == [locationDTO3]
    }

    def "addFriendToLocation should add a friend to the location and return OK status"() {
        given:
        def userId = 1
        def friendEmail = "friend@example.com"
        def locationAddress = "123 Main St, Springfield, IL, 62704"
        def accessLevel = AccessLevel.READ_ONLY

        when:
        def response = userController.addFriendToLocation(userId, friendEmail, locationAddress, accessLevel)

        then:
        1 * locationAccessService.addFriendToLocation(userId, friendEmail, locationAddress, accessLevel)
        response.statusCode == HttpStatus.OK
        response.body == "Friend added successfully."
    }
}
