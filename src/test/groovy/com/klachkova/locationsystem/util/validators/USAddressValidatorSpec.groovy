package com.klachkova.locationsystem.util.validators

import spock.lang.Specification

import javax.validation.ConstraintValidatorContext

class USAddressValidatorSpec extends Specification {

    def validator = new USAddressValidator()
    def context = Mock(ConstraintValidatorContext)

    def "test null address"() {

        when:
            def result = validator.isValid(null, context)

        then:
            !result
    }

    def "test empty address"() {

        when:
            def result = validator.isValid("", context)

        then:
            !result
    }

    def "test valid US address"() {

        given:
            def validAddress = "123 Main St, Springfield, IL, 62704"

        expect:
            validator.isValid(validAddress, context)
    }

    def "test valid US address with ZIP+4"() {

        given:
            def validAddressWithZipPlus4 = "123 Main St, Springfield, IL, 62704-1234"

        expect:
            validator.isValid(validAddressWithZipPlus4, context)
    }

    def "test invalid US address"() {

        given:
            def invalidAddress = "123 Main Street, Springfield, 62704"
            // Missing state

        when:
            def result = validator.isValid(invalidAddress, context)

        then:
            !result
    }

    def "test address with leading or trailing spaces"() {

        given:
            def addressWithSpaces = " 123 Main St, Springfield, IL, 62704 "

        expect:
            validator.isValid(addressWithSpaces.trim(), context) // Should pass when trimmed
    }
}