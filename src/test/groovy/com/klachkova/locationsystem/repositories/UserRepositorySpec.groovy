package com.klachkova.locationsystem.repositories

import com.klachkova.locationsystem.modeles.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

@DataJpaTest
class UserRepositorySpec extends Specification {

    @Autowired
    UserRepository userRepository

    def "test existsByEmail returns true when user with email exists"() {

        given:
            def email = "existingUser@example.com"
            def user = new User(name: "name", email: email)
            userRepository.save(user)

        when:
            def result = userRepository.existsByEmail(email)

        then:
            result == true
    }

    def "test existsByEmail returns false when user with email does not exist"() {

        given:
            def email = "nonexistentUser@example.com"

        when:
            def result = userRepository.existsByEmail(email)

        then:
            result == false
    }

    def "test findByEmail returns user when user with email exists"() {

        given:
            def email = "existingUser@example.com"
            def user = new User(name: "name", email: email)
            userRepository.save(user)

        when:
            def result = userRepository.findByEmail(email)

        then:
            result.isPresent()
            result.get().email == email
            result.get().name == "name"
    }

    def "test findByEmail returns empty when user with email does not exist"() {

        given:
            def email = "nonexistentUser@example.com"

        when:
            def result = userRepository.findByEmail(email)

        then:
            !result.isPresent()
    }
}
