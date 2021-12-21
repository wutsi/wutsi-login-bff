package com.wutsi.application.login.endpoint.onboard.page

import com.wutsi.application.login.endpoint.AbstractQuery
import com.wutsi.application.login.service.OnboardService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractOnboardQuery : AbstractQuery() {
    @Autowired
    protected lateinit var service: OnboardService

    protected fun getPhoneNumber(): String {
        val state = service.getState()
        return formattedPhoneNumber(state.phoneNumber, state.country)!!
    }
}
