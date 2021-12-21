package com.wutsi.application.login.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class CountryDetectorTest {
    @Autowired
    lateinit var detector: CountryDetector

    @Test
    fun detectCA() {
        assertEquals("CA", detector.detect("+15147580111"))
        assertEquals("CA", detector.detect("+18197580111"))
        assertEquals("CA", detector.detect("+16135550196"))
    }

    @Test
    fun detectCM() {
        assertEquals("CM", detector.detect("+23799505678"))
    }

    @Test
    fun detectUS() {
        assertEquals("US", detector.detect("+15617580111"))
    }

    @Test
    fun detectUK() {
        assertEquals("GB", detector.detect("+442079460214"))
    }
}
