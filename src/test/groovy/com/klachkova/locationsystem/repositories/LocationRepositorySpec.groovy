package com.klachkova.locationsystem.repositories

import com.klachkova.locationsystem.modeles.Location
import com.klachkova.locationsystem.modeles.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

@DataJpaTest
class LocationRepositorySpec extends Specification {
    @Autowired
    LocationRepository locationRepository
    @Autowired
    UserRepository userRepository

    def "test findAllByOwner should return locations owned by a specific user"() {
        given:
        def user = new User(name: "name", email: "name@example.com")
        userRepository.save(user)
        def location1 = new Location(name: "Location 1", address: "123 Main St, Springfield, IL, 62704",owner: user)
        def location2 = new Location(name: "Location 2", address: "456 Main St, Springfield, IL, 62704",owner: user)
        locationRepository.saveAll([location1, location2])

        when:
        def locations = locationRepository.findAllByOwner(user)

        then:
        locations.size() == 2
        locations.contains(location1)
        locations.contains(location2)
    }

    def "test findAllByOwner should return an empty list if the user owns no locations"() {
        given:
        def user = new User(name: "name", email: "name@example.com")
        userRepository.save(user)

        when:
        def locations = locationRepository.findAllByOwner(user)

        then:
        locations.isEmpty()
    }

    def "test existsByAddress should return true if a location exists with the specified address"() {
        given:
        def user = new User(name: "name", email: "name@example.com")
        userRepository.save(user)
        def address = "123 Main St, Springfield, IL, 62704"
        def location = new Location(name: "Location 1", address: address, owner: user)
        locationRepository.save(location)

        when:
        def exists = locationRepository.existsByAddress(address)

        then: "it should return true"
        exists == true
    }

    def "test existsByAddress should return false if no location exists with the specified address"() {
        given:
        def address = "123 Main St, Springfield, IL, 62704"

        when:
        def exists = locationRepository.existsByAddress(address)

        then:
        exists == false
    }

    def "test findByAddress should return a location if it exists by the specified address"() {
        given:
        def user = new User(name: "name", email: "name@example.com")
        userRepository.save(user)
        def address = "123 Main St, Springfield, IL, 62704"
        def location = new Location( name: "Location 1", address: address,  owner: user)
        locationRepository.save(location)

        when:
        def result = locationRepository.findByAddress(address)

        then:
        result.isPresent()
        result.get().address == address
    }

    def "test findByAddress should return an empty Optional if no location exists by the specified address"() {
        given:
        def address = "123 Main St, Springfield, IL, 62704"

        when:
        def result = locationRepository.findByAddress(address)

        then:
        !result.isPresent()
    }

}
