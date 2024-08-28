package com.klachkova.locationsystem.services

import com.klachkova.locationsystem.dto.LocationDTO
import com.klachkova.locationsystem.dto.UserDTO
import com.klachkova.locationsystem.modeles.Location
import com.klachkova.locationsystem.modeles.User
import com.klachkova.locationsystem.repositories.LocationRepository
import com.klachkova.locationsystem.repositories.UserRepository
import com.klachkova.locationsystem.util.converters.LocationConverter
import com.klachkova.locationsystem.util.converters.UserConverter
import com.klachkova.locationsystem.util.exceptions.NotCreatedException
import com.klachkova.locationsystem.util.exceptions.NotFoundException
import com.klachkova.locationsystem.util.exceptions.ValidationException
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import javax.validation.ConstraintViolation
import javax.validation.Validator

class LocationServiceSpec extends Specification {

    UserRepository userRepository = Mock()
    UserConverter userConverter = Mock()
    LocationRepository locationRepository = Mock()
    LocationConverter locationConverter = Mock()
    LocationAccessService locationAccessService = Mock()
    Validator validator = Mock()


    @Subject
    LocationService locationService = new LocationService(
            userRepository,
            userConverter,
            locationRepository,
            locationConverter,
            locationAccessService,
            validator)

    def "test registerLocation saves location when all conditions are met"() {
        given:

        def email = "owner@example.com"
        def address = "123 Main St, Springfield, IL, 62704"
        def locationDTO = new LocationDTO(name: "Location Name", address: address, owner: new UserDTO(name: "Owner", email: email))
        def user = new User(name: "Owner", email: email)
        def location = new Location(name: "Location Name", address: address, owner: user)
        def savedLocation = new Location(id: 1, name: "Location Name", address: address, owner: user)
        def locationDTOAfterSave = new LocationDTO(name: "Location Name", address: address, owner: new UserDTO(name: "Owner", email: email))


        and:
        locationConverter.convertToEntity(locationDTO) >> location
        validator.validate(location) >> []
        userRepository.existsByEmail(email) >> true
        locationRepository.existsByAddress(address) >> false
        locationRepository.save(location) >> savedLocation
        locationConverter.convertToDto(savedLocation) >> locationDTOAfterSave

        when:
        def result = locationService.registerLocation(locationDTO)

        then:
        result == locationDTOAfterSave
    }

    def "test registerLocation should throw NotCreatedException if user does not exist"() {
        given:
        def email = "owner@example.com"
        def address = "123 Main St, Springfield, IL, 62704"
        def locationDTO = new LocationDTO(name: "Location Name", address: address, owner: new UserDTO(name: "Owner", email: email))
        def user = new User(name: "Owner", email: email)
        def location = new Location(name: "Location Name", address: address, owner: user)

        and:
        locationConverter.convertToEntity(locationDTO) >> location
        validator.validate(location) >> []
        userRepository.existsByEmail(email) >> false

        when:
        locationService.registerLocation(locationDTO)

        then:
        thrown(NotCreatedException)
    }

    def "test registerLocation should throw NotCreatedException if location address already exists"() {
        given:
        def email = "owner@example.com"
        def address = "123 Main St, Springfield, IL, 62704"
        def locationDTO = new LocationDTO(name: "Location Name", address: address, owner: new UserDTO(name: "Owner", email: email))
        def user = new User(name: "Owner", email: email)
        def location = new Location(name: "Location Name", address: address, owner: user)

        and:
        locationConverter.convertToEntity(locationDTO) >> location
        validator.validate(location) >> []
        userRepository.existsByEmail(email) >> true
        locationRepository.existsByAddress(address) >> true

        when:
        locationService.registerLocation(locationDTO)

        then:
        thrown(NotCreatedException)
    }

    def "test registerLocation should throw ValidationException if location is invalid"() {
        given:
        def locationDTO = new LocationDTO() //invalid
        def location = new Location()


        and:
        locationConverter.convertToEntity(locationDTO) >> location
        validator.validate(location) >> [Mock(ConstraintViolation)]

        when:
        locationService.registerLocation(locationDTO)

        then:
        thrown(ValidationException)
    }

    def "test findByAddress should return the location when found"() {
        given:
        def address = "123 Main St, Springfield, IL, 62704"
        def locationDTO = new LocationDTO(name: "Location Name", address: address, owner: new UserDTO())
        def user = new User()
        def location = new Location(name: "Location Name", address: address, owner: user)

        and:
        locationConverter.convertToEntity(locationDTO) >> location
        locationRepository.findByAddress(address) >> Optional.of(location)

        when:
        def result = locationService.findByAddress(address)

        then:
        result == location
    }

    def "test findByAddress should throw NotFoundException when location not found"() {
        given:
        def address = "123 Main St, Springfield, IL, 62704"
        def locationDTO = new LocationDTO()
        def location = new Location()

        and:
        locationConverter.convertToEntity(locationDTO) >> location
        locationRepository.findByAddress(address) >> Optional.empty()

        when:
        locationService.findByAddress(address)

        then:
        thrown(NotFoundException)
    }

    def "test findById should return the location when found"() {
        given:
        def locationId = 1
        def location = new Location(id: locationId)
        def locationDTO = new LocationDTO()

        and:
        locationConverter.convertToEntity(locationDTO) >> location
        locationRepository.findById(locationId) >> Optional.of(location)

        when:
        def result = locationService.findById(locationId)

        then:
        result == location
    }

    def "test findById should throw NotFoundException when location not found"() {
        given:
        def locationId = 1
        def location = new Location(id: locationId)
        def locationDTO = new LocationDTO()

        and:
        locationConverter.convertToEntity(locationDTO) >> location
        locationRepository.findById(locationId) >> Optional.empty()

        when:
        locationService.findById(locationId)

        then:
        thrown(NotFoundException)
    }

    @Unroll
    def "test existsByAddress should return #expectedResult when address is #address"() {
        given:
        def locationDTO = new LocationDTO()
        def location = new Location()

        and:
        locationConverter.convertToEntity(locationDTO) >> location
        locationRepository.existsByAddress(address) >> expectedResult

        expect:
        locationService.existsByAddress(address) == expectedResult

        where:
        address        | expectedResult
        "123 True St"  | true
        "456 False St" | false
    }

    def "test getAvailableLocations returns own and shared locations for existing user"() {
        given:
        def userId = 1
        def user = new User(id: userId, name: "User1")
        def ownLocations = [new Location(id: 1, name: "Location1", owner: user)]
        def sharedLocations = [new Location(id: 2, name: "SharedLocation", owner: new User())]
        def ownLocationDTOs = [new LocationDTO(name: "Location1")]
        def sharedLocationDTOs = [new LocationDTO(name: "SharedLocation")]

        userRepository.findById(userId) >> Optional.of(user)
        locationRepository.findAllByOwner(user) >> ownLocations
        locationAccessService.getAllSharedLocations(user) >> sharedLocations
        locationConverter.convertToDto(ownLocations[0]) >> ownLocationDTOs[0]
        locationConverter.convertToDto(sharedLocations[0]) >> sharedLocationDTOs[0]

        when:
        def result = locationService.getAvailableLocations(userId)

        then:
        result.size() == 2
        result[0] == ownLocationDTOs
        result[1] == sharedLocationDTOs
    }
    def "test getAvailableLocations returns only shared locations when user has no own locations"() {
        given:
        def userId = 1
        def user = new User(id: userId, name: "User1")
        def sharedLocations = [new Location(id: 2, name: "SharedLocation", owner: new User())]
        def sharedLocationDTOs = [new LocationDTO(name: "SharedLocation")]

        userRepository.findById(userId) >> Optional.of(user)
        locationRepository.findAllByOwner(user) >> []
        locationAccessService.getAllSharedLocations(user) >> sharedLocations
        locationConverter.convertToDto(sharedLocations[0]) >> sharedLocationDTOs[0]

        when:
        def result = locationService.getAvailableLocations(userId)

        then:
        result.size() == 2
        result[0].isEmpty()
        result[1] == sharedLocationDTOs
    }

    def "test getAvailableLocations returns empty lists when user has no own or shared locations"() {
        given:
        def userId = 1
        def user = new User(id: userId, name: "User1")

        userRepository.findById(userId) >> Optional.of(user)
        locationRepository.findAllByOwner(user) >> []
        locationAccessService.getAllSharedLocations(user) >> []

        when:
        def result = locationService.getAvailableLocations(userId)

        then:
        result.size() == 2
        result[0].isEmpty()
        result[1].isEmpty()
    }

    def "test getAvailableLocations throws NotFoundException when user does not exist"() {
        given:
        def userId = 1

        userRepository.findById(userId) >> Optional.empty()

        when:
        locationService.getAvailableLocations(userId)

        then:
        thrown(NotFoundException)
    }

    def "test getFriendsToLocation should return users with access to the location"() {
        given:
        def locationId = 1
        def user1 = new User()
        def user2 = new User()
        def user1DTO = new UserDTO()
        def user2DTO = new UserDTO()
        and:
        locationAccessService.getFriends(locationId) >> [user1, user2]
        userConverter.convertToDto(user1) >> user1DTO
        userConverter.convertToDto(user2) >> user2DTO

        when:
        def result = locationService.getFriendsToLocation(locationId)

        then:
        result.size() == 2
        result.contains(user1DTO)
        result.contains(user2DTO)
    }
}