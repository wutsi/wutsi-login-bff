package com.wutsi.application.shared.service

import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.stereotype.Service

@Service
class TenantProvider(
    private val tenantApi: WutsiTenantApi,
    private val tracingContext: TracingContext,
) {
    fun get(): Tenant =
        tenantApi.getTenant(tenantId()).tenant

    fun logo(tenant: Tenant): String? =
        tenant.logos.find { it.type == "PICTORIAL" }?.url

    private fun tenantId(): Long =
        tracingContext.tenantId()!!.toLong()
}
