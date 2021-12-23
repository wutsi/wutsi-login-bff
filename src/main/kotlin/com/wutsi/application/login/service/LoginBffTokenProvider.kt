package com.wutsi.application.login.service

import com.wutsi.platform.core.security.spring.ApplicationTokenProvider
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.AuthenticationRequest

class LoginBffTokenProvider(
    private val securityApi: WutsiSecurityApi,
    private val apiKey: String,
) : ApplicationTokenProvider("") {
    private var token: String? = null

    override fun getToken(): String? {
        if (token == null) {
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
