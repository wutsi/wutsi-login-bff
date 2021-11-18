package com.wutsi.application.login.endpoint

import com.wutsi.application.login.exception.AuthenticationException
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ActionType.Prompt
import com.wutsi.flutter.sdui.enums.ActionType.Route
import com.wutsi.flutter.sdui.enums.DialogType.Error
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.ExceptionHandler

abstract class AbstractEndpoint {
    @Autowired
    private lateinit var messages: MessageSource

    @Autowired
    private lateinit var logger: KVLogger

    @ExceptionHandler(AuthenticationException::class)
    fun onAuthenticationException(ex: AuthenticationException) =
        createErrorAction(ex, "message.error.login-failed")

    private fun createErrorAction(e: Throwable, messageKey: String): Action {
        val action = Action(
            type = Prompt,
            prompt = Dialog(
                title = getText("prompt.error.title"),
                type = Error,
                message = getText(messageKey)
            )
        )
        log(action, e)
        return action
    }

    private fun log(action: Action, e: Throwable) {
        logger.add("action_type", action.type)
        logger.add("action_url", action.url)
        logger.add("action_prompt_type", action.prompt?.type)
        logger.add("action_prompt_message", action.prompt?.message)
        logger.add("exception", e::class.java)
        logger.add("exception_message", e.message)

        if (e is AuthenticationException) {
            logger.add("error_code", e.error?.code)
        }
    }

    protected fun getText(key: String, args: Array<Any?> = emptyArray()) =
        messages.getMessage(key, args, LocaleContextHolder.getLocale())

    protected fun gotoRoute(path: String, replacement: Boolean? = null) = Action(
        type = Route,
        url = "route:$path",
        replacement = replacement
    )

    protected fun gotoUrl(url: String, type: ActionType) = Action(
        type = type,
        url = url
    )
}
