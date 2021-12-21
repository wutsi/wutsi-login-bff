package com.wutsi.application.login.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.login.endpoint.onboard.dto.SavePinRequest
import com.wutsi.application.login.endpoint.onboard.dto.SaveProfileRequest
import com.wutsi.application.login.endpoint.onboard.dto.SendSmsCodeRequest
import com.wutsi.application.login.endpoint.onboard.dto.VerifySmsCodeRequest
import com.wutsi.application.login.entity.AccountEntity
import com.wutsi.application.login.exception.PhoneAlreadyAssignedException
import com.wutsi.application.login.exception.PinMismatchException
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.CreateAccountRequest
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_HEADER
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.tracing.spring.RequestTracingContext
import com.wutsi.platform.core.util.URN
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.AuthenticationRequest
import com.wutsi.platform.sms.WutsiSmsApi
import com.wutsi.platform.sms.dto.SendVerificationRequest
import feign.FeignException
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.Cache
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class OnboardService(
    private val smsApi: WutsiSmsApi,
    private val accountApi: WutsiAccountApi,
    private val securityApi: WutsiSecurityApi,
    private val logger: KVLogger,
    private val applicationProvider: ApplicationProvider,
    private val mapper: ObjectMapper,
    private val tracingContext: RequestTracingContext,
    private val countryDetector: CountryDetector,
    private val cache: Cache,

    @Value("\${wutsi.toggles.send-sms-code}") private val toggleSendSmsCode: Boolean,
    @Value("\${wutsi.toggles.verification}") private val toggleVerify: Boolean,
) {
    companion object {
        val ACCOUNT_ALREADY_ASSIGNED: String = URN.of("error", "account", "phone-number-already-assigned").value
        val DEVICE_NOT_FOUND: String = URN.of("error", "app-onboard", "device-not-found").value
    }

    fun getState(): AccountEntity =
        cache.get(tracingContext.deviceId(), AccountEntity::class.java)
            ?: throw NotFoundException(
                error = Error(
                    code = DEVICE_NOT_FOUND,
                    parameter = Parameter(
                        name = TracingContext.HEADER_DEVICE_ID,
                        type = PARAMETER_TYPE_HEADER,
                        value = tracingContext.deviceId()
                    )
                )
            )

    fun save(state: AccountEntity) {
        cache.put(tracingContext.deviceId(), state)
    }

    fun resendSmsCode() {
        val state = getState()
        sendSmsCode(
            SendSmsCodeRequest(
                phoneNumber = state.phoneNumber
            )
        )
    }

    fun sendSmsCode(request: SendSmsCodeRequest) {
        val phoneNumber = request.phoneNumber
        val country = countryDetector.detect(phoneNumber)
        val language = LocaleContextHolder.getLocale().language
        try {
            // Send verification
            val verificationId: Long = if (toggleSendSmsCode)
                smsApi.sendVerification(
                    SendVerificationRequest(
                        phoneNumber = phoneNumber,
                        language = language
                    )
                ).id
            else
                -1
            logger.add("verification_id", verificationId)

            // Update state
            save(
                AccountEntity(
                    deviceId = tracingContext.deviceId(),
                    phoneNumber = phoneNumber,
                    country = country,
                    language = language,
                    verificationId = verificationId
                )
            )
        } finally {
            logger.add("phone_number", phoneNumber)
            logger.add("country", country)
            logger.add("language", language)
        }
    }

    fun verifyCode(request: VerifySmsCodeRequest) {
        logger.add("verification_code", request.code)

        val state = getState()
        if (toggleVerify) {
            smsApi.validateVerification(
                id = state.verificationId,
                code = request.code
            )
        }

        if (findAccount(state) != null) {
            throw PhoneAlreadyAssignedException()
        }
    }

    fun createProfile(request: SaveProfileRequest) {
        logger.add("display_name", request.displayName)

        // Update
        val state = getState()
        state.displayName = request.displayName
        save(state)
    }

    fun savePin(request: SavePinRequest) {
        logger.add("pin", "***")

        val state = getState()
        state.pin = request.pin
        save(state)
    }

    fun confirmPin(request: SavePinRequest) {
        logger.add("pin", "***")

        val state = getState()
        if (state.pin != request.pin)
            throw PinMismatchException()
    }

    fun createWallet(): String {
        try {
            val state = getState()
            createAccount(state)
            return authenticate(state)
        } catch (ex: FeignException) {
            val response = toErrorResponse(ex)
            if (response.error.code == ACCOUNT_ALREADY_ASSIGNED) {
                throw PhoneAlreadyAssignedException()
            }
            throw ex
        }
    }

    private fun findAccount(state: AccountEntity): AccountSummary? {
        val accounts = accountApi.searchAccount(SearchAccountRequest(phoneNumber = state.phoneNumber)).accounts
        return if (accounts.isNotEmpty())
            accounts[0]
        else
            null
    }

    private fun createAccount(state: AccountEntity): Long {
        val accountId = accountApi.createAccount(
            CreateAccountRequest(
                phoneNumber = state.phoneNumber,
                displayName = state.displayName,
                language = state.language,
                country = state.country,
                password = state.pin,
                addPaymentMethod = true
            )
        ).id
        logger.add("account_id", accountId)
        return accountId
    }

    private fun authenticate(state: AccountEntity): String {
        val accessToken = securityApi.authenticate(
            AuthenticationRequest(
                type = "runas",
                phoneNumber = state.phoneNumber,
                apiKey = applicationProvider.get().apiKey
            )
        ).accessToken
        logger.add("access_token", "******")

        return accessToken
    }

    private fun toErrorResponse(ex: FeignException): ErrorResponse {
        val buff = ex.responseBody().get()
        val bytes = ByteArray(buff.remaining())
        buff.get(bytes)
        return mapper.readValue(bytes, ErrorResponse::class.java)
    }
}
