package com.klachkova.locationsystem.util.validators

import com.klachkova.locationsystem.modeles.AccessLevel
import com.klachkova.locationsystem.util.annotations.ValidAccessLevel
import spock.lang.Specification
import javax.validation.ConstraintValidatorContext

class AccessLevelValidatorSpec extends Specification {

    def validator = new AccessLevelValidator()

    def setup() {

        validator.initialize(Mock(ValidAccessLevel))
    }

    def "test valid AccessLevel READ_ONLY"() {

        given:
            def context = Mock(ConstraintValidatorContext)
            def validAccessLevel = AccessLevel.READ_ONLY

        expect:
            validator.isValid(validAccessLevel, context)
    }

    def "test valid AccessLevel ADMIN"() {

        given:
            def context = Mock(ConstraintValidatorContext)
            def validAccessLevel = AccessLevel.ADMIN

        expect:
            validator.isValid(validAccessLevel, context)
    }
}