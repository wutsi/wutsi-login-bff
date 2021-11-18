package com.wutsi.application.login

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.tracing.spring.SpringTracingRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.client.RestTemplate
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

abstract class AbstractEndpointTest {
    companion object {
        val DEVICE_ID = UUID.randomUUID().toString()
        const val LOCAL_PHONE_NUMBER = "15147550011"
        const val PHONE_NUMBER = "+$LOCAL_PHONE_NUMBER"
    }

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockBean
    private lateinit var tracingContext: TracingContext

    @Autowired
    private lateinit var messages: MessageSource

    protected lateinit var rest: RestTemplate

    lateinit var traceId: String

    @BeforeTest
    open fun setUp() {
        traceId = UUID.randomUUID().toString()
        doReturn(DEVICE_ID).whenever(tracingContext).deviceId()
        doReturn(traceId).whenever(tracingContext).traceId()
        doReturn("1").whenever(tracingContext).tenantId()

        rest = RestTemplate()
        rest.interceptors.add(SpringTracingRequestInterceptor(tracingContext))
    }

    protected fun assertEndpointEquals(expectedPath: String, url: String) {
        val request = emptyMap<String, String>()
        val response = rest.postForEntity(url, request, Map::class.java)

        assertJsonEquals(expectedPath, response.body)
    }

    private fun assertJsonEquals(expectedPath: String, value: Any?) {
        val input = AbstractEndpointTest::class.java.getResourceAsStream(expectedPath)
        val expected = mapper.readValue(input, Any::class.java)

        val writer = mapper.writerWithDefaultPrettyPrinter()
        assertEquals(writer.writeValueAsString(expected), writer.writeValueAsString(value))
    }

    protected fun getText(key: String, args: Array<Any?> = emptyArray()) =
        messages.getMessage(key, args, LocaleContextHolder.getLocale()) ?: key
}
