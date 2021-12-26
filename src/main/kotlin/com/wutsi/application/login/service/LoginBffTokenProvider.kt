package com.wutsi.application.login.service

import com.wutsi.platform.core.security.TokenProvider
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.AuthenticationRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class LoginBffTokenProvider(
    private val securityApi: WutsiSecurityApi,

    @Value("\${wutsi.platform.security.api-key}") val apiKey: String
) : TokenProvider {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LoginBffTokenProvider::class.java)
    }

    private var token: String? = null

    override fun getToken(): String? {
        if (token == null) {
            LOGGER.info("Authenticating...")
            token = securityApi.authenticate(
                AuthenticationRequest(
                    type = "application",
                    apiKey = apiKey
                )
            ).accessToken
        }

        return token
    }
}
