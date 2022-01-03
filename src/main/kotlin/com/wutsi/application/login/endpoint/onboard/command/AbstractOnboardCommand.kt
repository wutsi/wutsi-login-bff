package com.wutsi.application.login.endpoint.onboard.command

import com.wutsi.application.login.endpoint.AbstractCommand
import com.wutsi.application.login.exception.PhoneAlreadyAssignedException
import com.wutsi.application.login.service.OnboardService
import com.wutsi.application.login.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler

abstract class AbstractOnboardCommand : AbstractCommand() {
    @Autowired
    protected lateinit var service: OnboardService

    @Autowired
    protected lateinit var urlBuilder: URLBuilder

    @ExceptionHandler(PhoneAlreadyAssignedException::class)
    fun onPhoneAlreadyAssignedException(e: PhoneAlreadyAssignedException): Action {
        val state = service.getState()
        val action = gotoUrl(
            url = urlBuilder.build(
                "/login" +
                    "?title=" + encodeURLParam(getText("page.login.title")) +
                    "&sub-title=" + encodeURLParam(getText("page.login.sub-title")) +
                    "&phone=" + encodeURLParam(state.phoneNumber)
            ),
            type = ActionType.Route,
            replacement = true
        )
        log(action, e)
        return action
    }

    @ExceptionHandler(NotFoundException::class)
    fun onNotFoundException(e: NotFoundException): ResponseEntity<Action> {
        if (e.error.code == OnboardService.DEVICE_NOT_FOUND) {
            val action = gotoPage(com.wutsi.application.login.endpoint.Page.PHONE)
            log(action, e)
            return ResponseEntity.ok(action)
        } else {
            log(e)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}
