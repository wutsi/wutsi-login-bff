package com.wutsi.application.login.endpoint.onboard.command

import com.wutsi.flutter.sdui.Action
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/commands/resend-sms-code")
class ResendSmsCodeCommand : AbstractOnboardCommand() {
    @PostMapping
    fun submit(): Action {
        service.resendSmsCode()
        return promptInformation("message.info.code-resent")
    }
}
