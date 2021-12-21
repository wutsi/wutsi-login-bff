package com.wutsi.application.login.endpoint.onboard.page

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.application.login.endpoint.AbstractQuery
import com.wutsi.application.login.service.OnboardService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractOnboardQuery : AbstractQuery() {
    @Autowired
    protected lateinit var service: OnboardService

    protected fun getPhoneNumber(): String {
        val state = service.getState()
        val phoneNumber = state.phoneNumber
        val country = state.country
        return try {
            val util = PhoneNumberUtil.getInstance()
            val phone = util.parse(phoneNumber, country)
            util.format(phone, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        } catch (ex: Exception) {
            phoneNumber
        }
    }
}
