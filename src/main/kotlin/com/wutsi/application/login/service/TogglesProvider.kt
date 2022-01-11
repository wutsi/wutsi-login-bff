package com.wutsi.application.login.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@ConfigurationProperties(prefix = "wutsi.toggles")
class Toggles {
    var sendSmsCode: Boolean = true
    var verifySmsCode: Boolean = true
    var payment: Boolean = true
    var scan: Boolean = true
    var account: Boolean = true
    var business: Boolean = true
    var logout: Boolean = true
    var testerUserIds: List<Long> = emptyList()
    var testPhoneNumbers: List<String> = emptyList()
}

@Service
@EnableConfigurationProperties(Toggles::class)
class TogglesProvider(
    private val toggles: Toggles,
) {
    fun isBusinessAccountEnabled(): Boolean =
        toggles.business

    fun isSendSmsEnabled(phoneNumber: String): Boolean =
        toggles.sendSmsCode || isTestTestPhoneNumber(phoneNumber)

    fun isVerifySmsCodeEnabled(phoneNumber: String): Boolean =
        toggles.verifySmsCode || isTestTestPhoneNumber(phoneNumber)

    fun isAccountEnabled(): Boolean =
        toggles.account

    fun isLogoutEnabled(): Boolean =
        toggles.logout || isTester()

    private fun isTester(): Boolean =
        false

    private fun isTestTestPhoneNumber(phoneNumber: String): Boolean =
        toggles.testPhoneNumbers.contains(phoneNumber)
}
