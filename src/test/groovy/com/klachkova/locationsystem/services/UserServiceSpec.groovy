package com.klachkova.locationsystem.services

import com.klachkova.locationsystem.modeles.User
import com.klachkova.locationsystem.repositories.UserRepository
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import com.klachkova.locationsystem.util.NotCreatedException


class UserServiceSpec extends Specification {
    UserRepository userRepository = Mock(UserRepository)
    @Subject
    UserService userService = new UserService(userRepository)

    def "test registerUser saves user"() {
        given:
        def email = "name1@example.com"
        def user = new User(email: email)

        and:
        userRepository.existsByEmail(email) >> false

        when:
        userService.registerUser(user)

        then:
        1 * userRepository.save(user)
    }

    def "test registerUser should throw NotCreatedException if user with email already exists"() {
        given:
        def email = "name1@example.com"
        def user = new User(email: email)

        and:
        userRepository.existsByEmail(email) >> true

        when:
        userService.registerUser(user)

        then:
        thrown(NotCreatedException)
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
        def user = new User(id: 1)

        and:
        userRepository.findById(1) >> Optional.of(user)

        when:
        def result = userService.findById(1)

        then:
        result == user
    }

    def "test findById should throw NoSuchElementException when user not found"() {
        given:
        userRepository.findById(10) >> Optional.empty()

        when:
        userService.findById(10)

        then:
        thrown(NoSuchElementException)
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

    def "test findByEmail should throw NoSuchElementException when user not found"() {
        given:
        def email = "name1@example.com"

        and:
        userRepository.findByEmail(email) >> Optional.empty()

        when:
        userService.findByEmail(email)

        then:
        thrown(NoSuchElementException)
    }

    @Unroll
    def "test existsByEmail should return #expectedResult when email is #email"() {
        given:
        userRepository.existsByEmail(email) >> expectedResult

        expect:
        userService.existsByEmail(email) == expectedResult

        where:
        email               | expectedResult
        "name1@example.com" | true
        "name2@example.com" | false
    }
}
