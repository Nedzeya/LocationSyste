package com.klachkova.locationsystem.services

import com.klachkova.locationsystem.modeles.User
import com.klachkova.locationsystem.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll


class UserServiceSpec extends Specification {

    @Autowired
    UserRepository userRepository = Mock(UserRepository)

    @Subject
    UserService userService = new UserService(userRepository)

    def "test registerUser saves user"()

    {
        given:
        def user = new User("name1", "newuser@example.com")

        when:
        userService.registerUser(user)

        then:
        1 * userRepository.save(user)
    }

    def "test findAll returns all users"()

    {
        given:
        def user1 = new User("name1", "user1@example.com")
        def user2 = new User("name2", "user2@example.com")
        userRepository.findAll() >> [user1, user2]

        when:
        def result = userService.findAll()

        then:
        result.size() == 2
        result[0].email == "user1@example.com"
        result[1].email == "user2@example.com"
    }

    def "test findById should return the user when found"()

    {
        given:

        def user = new User("name1", "user1@example.com")
        user.setId(1)
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
        def user = new User("name1", email)
        userRepository.findByEmail(email) >> Optional.of(user)

        when:
        def result = userService.findByEmail(email)

        then:
        result == user
    }

    def "test findByEmail should throw NoSuchElementException when user not found"() {


        given:
        def email = "name1@example.com"
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
