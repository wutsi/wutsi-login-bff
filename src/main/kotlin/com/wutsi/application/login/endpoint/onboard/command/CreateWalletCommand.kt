package com.wutsi.application.login.endpoint.onboard.command

import com.wutsi.flutter.sdui.Action
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/create-wallet")
class CreateWalletCommand : AbstractOnboardCommand() {
    @PostMapping
    fun submit(): ResponseEntity<Action> {
        val accessToken = service.createWallet()

        val headers = HttpHeaders()
        headers["x-access-token"] = accessToken
        headers["x-onboarded"] = "true"
        return ResponseEntity
            .ok()
            .headers(headers)
            .body(
                gotoRoute("/", true)
            )
    }
}
