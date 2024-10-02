package com.klachkova.locationsystem.controllers

import com.klachkova.locationsystem.dto.UserDTO
import com.klachkova.locationsystem.dto.LocationDTO
import com.klachkova.locationsystem.modeles.AccessLevel
import com.klachkova.locationsystem.services.LocationAccessService
import com.klachkova.locationsystem.services.LocationService
import com.klachkova.locationsystem.services.UserService
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class UserControllerSpec extends Specification {

    UserService userService = Mock()
    LocationService locationService = Mock()
    LocationAccessService locationAccessService = Mock()

    @Subject
    def userController = new UserController(
        userService,
        locationService,
        locationAccessService)

    def "test registerUser should create a user and return CREATED status and UserDTO"() {

        given:
            def userDTO = new UserDTO()

        and:
            userService.registerUser(userDTO) >> userDTO

        when:
            def response = userController.registerUser(userDTO)

        then:
            response.statusCode == HttpStatus.CREATED
            response.body == userDTO
    }

    def "test getAvailableLocations returns correct response"() {

        given:
            def userId = 1
            def locationDTO1 = new LocationDTO()
            def locationDTO2 = new LocationDTO()
            def locationDTO3 = new LocationDTO()
            def ownLocationsDTOs = [locationDTO1, locationDTO2]
            def sharedLocationsDTOs = [locationDTO3]
            def availableLocations = [ownLocationsDTOs, sharedLocationsDTOs]
        and:
            locationService.getAvailableLocations(userId) >> availableLocations
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
