package com.klachkova.locationsystem.services

import com.klachkova.locationsystem.dto.UserDTO
import com.klachkova.locationsystem.modeles.User
import com.klachkova.locationsystem.repositories.UserRepository
import com.klachkova.locationsystem.util.converters.UserConverter
import com.klachkova.locationsystem.util.exceptions.NotFoundException
import com.klachkova.locationsystem.util.exceptions.ValidationException
import spock.lang.Specification
import spock.lang.Subject
import com.klachkova.locationsystem.util.exceptions.NotCreatedException
import javax.validation.ConstraintViolation
import javax.validation.Validator

class UserServiceSpec extends Specification {

    UserRepository userRepository = Mock()
    Validator validator = Mock()
    UserConverter userConverter = Mock()

    @Subject
    UserService userService = new UserService(userRepository, validator, userConverter)

    def "test registerUser saves user when email does not exist"() {
        given:
        def email = "name@example.com"
        def name = "Name"
        def userDTO = new UserDTO(name: name, email: email)
        def user = new User(name: name, email: email)
        def savedUser = new User(id: 1, name: name, email: email)
        def userDTOAfterSave = new UserDTO(name: name, email: email)

        and:
        userConverter.convertToEntity(userDTO) >> user
        validator.validate(user) >> []
        userRepository.existsByEmail(email) >> false
        userRepository.save(user) >> savedUser
        userConverter.convertToDto(savedUser) >> userDTOAfterSave

        when:
        def result = userService.registerUser(userDTO)

        then:
        result == userDTOAfterSave
    }

    def "test registerUser should throw NotCreatedException if user with email already exists"() {
        given:
        def email = "name@example.com"
        def userDTO = new UserDTO(name: "Name", email: email)
        def user = new User(name: "Name", email: email)

        and:
        userConverter.convertToEntity(userDTO) >> user
        validator.validate(user) >> []
        userRepository.existsByEmail(email) >> true

        when:
        userService.registerUser(userDTO)

        then:
        thrown(NotCreatedException)
    }

    def "test registerUser should not register user if validation fails"() {
        given:
        def email = "name@example.com"
        def userDTO = new UserDTO(name: "Name", email: email)
        def user = new User(name: "Name", email: email)
        def violations = [Mock(ConstraintViolation)]

        and:
        userConverter.convertToEntity(userDTO) >> user
        validator.validate(user) >> violations

        when:
        userService.registerUser(userDTO)

        then:
        thrown(ValidationException)
    }

    def "test findAll returns all users"() {
        given:
        def user1 = new User(email: "user1@example.com")
        def user2 = new User(email: "user2@example.com")

        and:
        userRepository.findAll() >> [user1, user2]

        when:
        def result = userService.findAll()

        then:
        result.size() == 2
        result[0].email == "user1@example.com"
        result[1].email == "user2@example.com"
    }

    def "test findById should return the user when found"() {
        given:
        def userId = 1
        def user = new User(id: userId)

        and:
        userRepository.findById(userId) >> Optional.of(user)

        when:
        def result = userService.findById(userId)

        then:
        result == user
    }

    def "test findById should throw NotFoundException when user not found"() {
        given:
        def userId = 1
        userRepository.findById(userId) >> Optional.empty()

        when:
        userService.findById(userId)

        then:
        thrown(NotFoundException)
    }

    def "test findByEmail should return the user when found"() {
        given:
        def email = "name1@example.com"
        def user = new User(email: email)

        and:
        userRepository.findByEmail(email) >> Optional.of(user)

        when:
        def result = userService.findByEmail(email)

        then:
        result == user
    }

    def "test findByEmail should throw NotFoundException when user not found"() {
        given:
        def email = "name1@example.com"

        and:
        userRepository.findByEmail(email) >> Optional.empty()

        when:
        userService.findByEmail(email)

        then:
        thrown(NotFoundException)
    }
}