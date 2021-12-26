package com.wutsi.application.login.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.AuthenticationRequest
import com.wutsi.platform.security.dto.AuthenticationResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class BffTokenProviderTest {
    private lateinit var securityApi: WutsiSecurityApi

    private lateinit var provider: BffTokenProvider

    private val apiKey: String = "api-key"

    @BeforeEach
    fun setUp() {
        securityApi = mock()
        provider = BffTokenProvider(securityApi, apiKey)
    }

    @Test
    fun getToken() {
        doReturn(AuthenticationResponse(accessToken = "xxx")).whenever(securityApi).authenticate(any())

        val token = provider.getToken()

        assertEquals("xxx", token)

        val request = argumentCaptor<AuthenticationRequest>()
        verify(securityApi).authenticate(request.capture())
        assertEquals(apiKey, request.firstValue.apiKey)
        assertEquals("application", request.firstValue.type)
    }

    @Test
    fun getTokenCached() {
        doReturn(AuthenticationResponse(accessToken = "yyy")).whenever(securityApi).authenticate(any())

        provider.getToken()
        provider.getToken()
        provider.getToken()
        val token = provider.getToken()

        assertEquals("yyy", token)

        verify(securityApi).authenticate(any())
    }
}
