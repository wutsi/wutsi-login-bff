package com.wutsi.application.login.endpoint.command

import com.wutsi.application.login.dto.LoginRequest
import com.wutsi.application.login.endpoint.AbstractCommand
import com.wutsi.application.login.service.LoginService
import com.wutsi.flutter.sdui.Action
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/commands/login")
class LoginCommand(
    private val service: LoginService,
) : AbstractCommand() {
    @PostMapping
    fun submit(
        @RequestParam(name = "phone") phoneNumber: String,
        @RequestParam(name = "auth", required = false, defaultValue = "true") auth: Boolean = true,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<Action> {
        val accessToken = service.login(service.sanitizePhoneNumber(phoneNumber), auth, request)

        val headers = HttpHeaders()
        if (accessToken != null) {
            headers["x-access-token"] = accessToken
        }
        return ResponseEntity
            .ok()
            .headers(headers)
            .body(
                returnUrl?.let { gotoUrl(it) } ?: gotoRoute("/", true)
            )
    }
}
