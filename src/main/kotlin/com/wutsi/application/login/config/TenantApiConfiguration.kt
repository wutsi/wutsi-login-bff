package com.wutsi.application.login.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.login.service.BffTokenProvider
import com.wutsi.platform.core.security.feign.FeignAuthorizationRequestInterceptor
import com.wutsi.platform.core.tracing.feign.FeignTracingRequestInterceptor
import com.wutsi.platform.tenant.Environment.PRODUCTION
import com.wutsi.platform.tenant.Environment.SANDBOX
import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.WutsiTenantApiBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

@Configuration
public class TenantApiConfiguration(
    private val tokenProvider: BffTokenProvider,
    private val tracingRequestInterceptor: FeignTracingRequestInterceptor,
    private val mapper: ObjectMapper,
    private val env: Environment
) {
    @Bean
    fun tenantApi(): WutsiTenantApi =
        WutsiTenantApiBuilder().build(
            env = environment(),
            mapper = mapper,
            interceptors = listOf(
                tracingRequestInterceptor,
                FeignAuthorizationRequestInterceptor(tokenProvider)
            )
        )

    private fun environment(): com.wutsi.platform.tenant.Environment =
        if (env.acceptsProfiles(Profiles.of("prod")))
            PRODUCTION
        else
            SANDBOX
}