package com.wutsi.application.login.config

import com.wutsi.application.login.service.LoginBffTokenProvider
import com.wutsi.platform.core.security.TokenProvider
import com.wutsi.platform.core.security.spring.ApplicationTokenProvider
import com.wutsi.platform.security.WutsiSecurityApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class ApplicationTokenConfiguration(
    private val securityApi: WutsiSecurityApi,

    @Value("\${wutsi.platform.security.api-key}") val apiKey: String
) {
    /**
     * Override the instance of `TokenProvider`
     */
    @Primary
    @Bean
    fun bffTokenProvider(): TokenProvider =
        LoginBffTokenProvider(securityApi, apiKey)

    /**
     * Override the instance of `ApplicationTokenProvider`
     */
    @Primary
    @Bean
    fun bffApplicationTokenProvider(): ApplicationTokenProvider =
        LoginBffTokenProvider(securityApi, apiKey)
}
