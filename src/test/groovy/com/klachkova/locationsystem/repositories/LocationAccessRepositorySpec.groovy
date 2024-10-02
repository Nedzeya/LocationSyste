package com.klachkova.locationsystem.repositories

import com.klachkova.locationsystem.modeles.AccessLevel
import com.klachkova.locationsystem.modeles.Location
import com.klachkova.locationsystem.modeles.LocationAccess
import com.klachkova.locationsystem.modeles.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

@DataJpaTest
class LocationAccessRepositorySpec extends Specification {

    @Autowired
    LocationAccessRepository locationAccessRepository

    @Autowired
    LocationRepository locationRepository

    @Autowired
    UserRepository userRepository

    def "test findByUser should return location accesses for a specific user"() {

        given:
            def owner = new User(name: "name", email: "name@example.com")
            userRepository.save(owner)
            def location = new Location(name: "Location 1", address: "123 Main St, Springfield, IL, 62704", owner: owner)
            locationRepository.save(location)
            def user = new User(name: "name1", email: "name1@example.com")
            userRepository.save(user)
            def access1 = new LocationAccess(user: user, location: location, accessLevel: AccessLevel.READ_ONLY)
            def access2 = new LocationAccess(user: user, location: location, accessLevel: AccessLevel.ADMIN)
            locationAccessRepository.saveAll([access1, access2])

        when:
            def accesses = locationAccessRepository.findByUser(user)

        then:
            accesses.size() == 2
            accesses.every { it.user == user }
    }

    def "test findByLocationId should return location accesses for a specific location"() {

        given:
            def owner = new User(name: "name", email: "name@example.com")
            userRepository.save(owner)
            def location = new Location(name: "Location 1", address: "123 Main St, Springfield, IL, 62704", owner: owner)
            locationRepository.save(location)
            def user1 = new User(name: "name1", email: "name1@example.com")
            userRepository.save(user1)
            def user2 = new User(name: "name2", email: "name2@example.com")
            userRepository.save(user2)
            def access1 = new LocationAccess(user: user1, location: location, accessLevel: AccessLevel.READ_ONLY)
            def access2 = new LocationAccess(user: user2, location: location, accessLevel: AccessLevel.ADMIN)
            locationAccessRepository.saveAll([access1, access2])

        when:
            def accesses = locationAccessRepository.findByLocationId(location.getId())

        then: "It should return the correct location accesses"
            accesses.size() == 2
            accesses.every { it.location.id == location.id }
    }

    def "test findByLocationAndUser should return the locationAccess for a specific location and user"() {

        given:
            def owner = new User(name: "name", email: "name@example.com")
            userRepository.save(owner)
            def location = new Location(name: "Location 1", address: "123 Main St, Springfield, IL, 62704", owner: owner)
            locationRepository.save(location)
            def user1 = new User(name: "name1", email: "name1@example.com")
            userRepository.save(user1)
            def access1 = new LocationAccess(user: user1, location: location, accessLevel: AccessLevel.READ_ONLY)
            locationAccessRepository.save(access1)

        when:
            def result = locationAccessRepository.findByLocationAndUser(location, user1)

        then:
            result.isPresent()
            result.get() == access1
    }

    def "test findByLocationAndUser should return empty for non-existent locationAccess with given location and user"() {

        given:
            def owner = new User(name: "name", email: "name@example.com")
            userRepository.save(owner)
            def location = new Location(name: "Location 1", address: "123 Main St, Springfield, IL, 62704", owner: owner)
            locationRepository.save(location)
            def user1 = new User(name: "name1", email: "name1@example.com")
            userRepository.save(user1)

        when:
            def result = locationAccessRepository.findByLocationAndUser(location, user1)

        then:
            result == Optional.empty()
    }
}
