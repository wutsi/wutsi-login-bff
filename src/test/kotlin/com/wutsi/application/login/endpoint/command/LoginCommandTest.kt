package com.wutsi.application.login.endpoint.command

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.login.AbstractEndpointTest
import com.wutsi.application.login.dto.LoginRequest
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.DialogType
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.Application
import com.wutsi.platform.security.dto.AuthenticationResponse
import com.wutsi.platform.security.dto.GetApplicationResponse
import feign.FeignException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import java.net.URLEncoder
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class LoginCommandTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var securityApi: WutsiSecurityApi

    @MockBean
    private lateinit var accountApi: WutsiAccountApi

    private lateinit var accessToken: String
    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val app = Application()
        doReturn(GetApplicationResponse(app)).whenever(securityApi).application(any())

        accessToken = UUID.randomUUID().toString()
        doReturn(AuthenticationResponse(accessToken = accessToken)).whenever(securityApi).authenticate(any())

        url = "http://localhost:$port/commands/login?phone=" + URLEncoder.encode(PHONE_NUMBER, "utf-8")
    }

    @Test
    fun submit() {
        // GIVEN
        val account = AccountSummary(id = System.currentTimeMillis(), status = "ACTIVE")
        doReturn(SearchAccountResponse(listOf(account))).whenever(accountApi).searchAccount(any(), any(), any())

        // WHEN
        val request = LoginRequest(pin = "123456")
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        assertEquals(listOf(accessToken), response.headers["x-access-token"])

        val action = response.body
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/", action.url)
        assertEquals(true, action.replacement)
    }

    @Test
    fun submitWithLocalPhone() {
        url = "http://localhost:$port/commands/login?phone=$LOCAL_PHONE_NUMBER"

        // GIVEN
        val account = AccountSummary(id = System.currentTimeMillis(), status = "ACTIVE")
        doReturn(SearchAccountResponse(listOf(account))).whenever(accountApi).searchAccount(any(), any(), any())

        // WHEN
        val request = LoginRequest(pin = "123456")
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        assertEquals(listOf(accessToken), response.headers["x-access-token"])

        val action = response.body
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/", action.url)
        assertEquals(true, action.replacement)
    }

    @Test
    fun submitWithPhoneHavingSpace() {
        url = "http://localhost:$port/commands/login?phone=$PHONE_NUMBER"

        // GIVEN
        val account = AccountSummary(id = System.currentTimeMillis(), status = "ACTIVE")
        doReturn(SearchAccountResponse(listOf(account))).whenever(accountApi).searchAccount(any(), any(), any())

        // WHEN
        val request = LoginRequest(pin = "123456")
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        assertEquals(listOf(accessToken), response.headers["x-access-token"])

        val action = response.body
        assertEquals(ActionType.Route, action.type)
        assertEquals("route:/", action.url)
        assertEquals(true, action.replacement)
    }

    @Test
    fun invalidPassword() {
        // GIVEN
        val account = AccountSummary(id = System.currentTimeMillis(), status = "ACTIVE")
        doReturn(SearchAccountResponse(listOf(account))).whenever(accountApi).searchAccount(any(), any(), any())

        val ex = mock<FeignException>()
        doThrow(ex).whenever(accountApi).checkPassword(any(), any())

        // WHEN
        val request = LoginRequest(pin = "777777")
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(DialogType.Error, action.prompt?.type)
    }

    @Test
    fun accountNotFound() {
        // GIVEN
        doReturn(SearchAccountResponse(emptyList())).whenever(accountApi).searchAccount(any(), any(), any())

        // WHEN
        val request = LoginRequest(pin = "777777")
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(DialogType.Error, action.prompt?.type)
    }

    @Test
    fun accountNotActive() {
        // GIVEN
        val account = AccountSummary(id = System.currentTimeMillis(), status = "SUSPENDED")
        doReturn(SearchAccountResponse(listOf(account))).whenever(accountApi).searchAccount(any(), any(), any())

        // WHEN
        val request = LoginRequest(pin = "123456")
        val response = rest.postForEntity(url, request, Action::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val action = response.body
        assertEquals(ActionType.Prompt, action.type)
        assertEquals(DialogType.Error, action.prompt?.type)
    }
}
