package com.klachkova.locationsystem.controllers

import com.klachkova.locationsystem.util.NotCreatedException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import spock.lang.Specification

class ControllerAdviceSpec extends Specification{

    def controllerAdvice = new ControllerAdvice()

    def "test handle NotCreatedException should return BAD REQUEST"() {
        given:
        def exception = new NotCreatedException("Custom error message")

        when:
        def responseEntity = controllerAdvice.handleException(exception)

        then:
        responseEntity.statusCode == HttpStatus.BAD_REQUEST
        responseEntity.body.message == "Custom error message"
    }

    def "test handle IllegalArgumentException should return BAD REQUEST"() {
        given:
        def exception = new IllegalArgumentException("Invalid argument error message")

        when:
        def responseEntity = controllerAdvice.handleException(exception)

        then:
        responseEntity.statusCode == HttpStatus.BAD_REQUEST
        responseEntity.body.message == "Invalid argument error message"
    }

    def "test handle NoSuchElementException should return NOT FOUND"() {
        given:
        def exception = new NoSuchElementException("Element not found error message")

        when:
        def responseEntity = controllerAdvice.handleException(exception)

        then:
        responseEntity.statusCode == HttpStatus.NOT_FOUND
        responseEntity.body.message == "Element not found error message"
    }

  /*  def "test handle MethodArgumentNotValidException should return BAD REQUEST"() {
        given:
        def exception = new MethodArgumentNotValidException(null, null) {
            @Override
            String getMessage() {
                return "Method argument not valid error message"
            }
        }

        when:
        def responseEntity = controllerAdvice.handleException(exception)

        then:
        responseEntity.statusCode == HttpStatus.BAD_REQUEST
        responseEntity.body.message == "Method argument not valid error message"
    }
*/
    def "test handle SecurityException should return BAD REQUEST"() {
        given:
        def exception = new SecurityException("Security error message")

        when:
        def responseEntity = controllerAdvice.handleException(exception)

        then:
        responseEntity.statusCode == HttpStatus.BAD_REQUEST
        responseEntity.body.message == "Security error message"
    }
}
