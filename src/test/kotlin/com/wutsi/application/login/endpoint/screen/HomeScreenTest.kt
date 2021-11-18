package com.wutsi.application.login.endpoint.screen

import com.wutsi.application.login.AbstractEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class HomeScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port?phone=+5147580000"
    }

    @Test
    fun defaultLoginScreen() = assertEndpointEquals("/screens/home.json", url)

    @Test
    fun customLoginScreen() {
        url = "http://localhost:$port?phone=+5147580000&title=Foo&sub-title=Yo+Man&icon=i_c_o_n&return-url=https://www.google.ca"
        assertEndpointEquals("/screens/home-custom.json", url)
    }
}
