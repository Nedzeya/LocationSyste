package com.klachkova.locationsystem.services

import com.klachkova.locationsystem.modeles.User
import com.klachkova.locationsystem.repositories.UserRepository
import spock.lang.Specification


class UserServiceSpec extends Specification {
    UserRepository userRepository = Mock()
    UserService userService = new UserService(userRepository)

    def "test findAll returns all users"()

    {
        given:
        def user1 = new User(1, "name1", "user1@example.com")
        def user2 = new User(2, "name2", "user2@example.com")
        userRepository.findAll() >> [user1, user2]

        when:
        def result = userService.findAll()

        then:
        result.size() == 2
        result[0].email == "user1@example.com"
        result[1].email == "user2@example.com"
    }

    def "test findOne returns user by ID"()

    {
        given:
        def user = new User(1, "name1", "user1@example.com")
        userRepository.findById(1) >> Optional.of(user)

        when:
        def result = userService.findOne(1)

        then:
        result.email == "user1@example.com"
    }

    def "test findOne returns user by email"()

    {
        given:
        def user = new User(1, "name1", "user1@example.com")
        userRepository.findByEmail("user1@example.com") >> user

        when:
        def result = userService.findOne("user1@example.com")

        then:
        result.email == "user1@example.com"
    }

    def "test registerUser saves user"()

    {
        given:
        def user = new User(1, "name1", "newuser@example.com")

        when:
        userService.registerUser(user)

        then:
        1 * userRepository.save(user)
    }
}





