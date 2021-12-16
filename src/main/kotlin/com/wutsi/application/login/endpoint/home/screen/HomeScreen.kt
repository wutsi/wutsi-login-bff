package com.wutsi.application.login.endpoint.home.screen

import com.wutsi.application.login.endpoint.AbstractQuery
import com.wutsi.application.login.endpoint.Page
import com.wutsi.application.login.endpoint.Theme
import com.wutsi.application.login.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment.center
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLEncoder

@RestController
@RequestMapping
class HomeScreen(
    private val urlBuilder: URLBuilder
) : AbstractQuery() {
    @PostMapping
    fun index(
        @RequestParam(name = "phone") phoneNumber: String,
        @RequestParam(name = "screen-id", required = false) screenId: String? = null,
        @RequestParam(name = "icon", required = false) icon: String? = null,
        @RequestParam(name = "title", required = false) title: String? = null,
        @RequestParam(name = "sub-title", required = false) subTitle: String? = null,
        @RequestParam(name = "return-url", required = false) returnUrl: String? = null,
        @RequestParam(name = "return-to-route", required = false, defaultValue = "true") returnToRoute: Boolean = true,
        @RequestParam(name = "auth", required = false, defaultValue = "true") auth: Boolean = true,
    ) = Screen(
        id = screenId ?: Page.HOME,
        appBar = AppBar(
            backgroundColor = Theme.WHITE_COLOR,
            foregroundColor = Theme.BLACK_COLOR,
            elevation = 0.0,
            title = title ?: getText("page.login.app-bar.title"),
        ),
        child = Container(
            alignment = Center,
            child = Column(
                children = listOf(
                    Container(
                        alignment = Center,
                        padding = 10.0,
                        child = Row(
                            children = listOf(
                                Container(
                                    padding = 5.0,
                                    child = Icon(
                                        code = icon ?: Theme.ICON_LOGIN,
                                        color = Theme.PRIMARY_COLOR,
                                        size = 16.0
                                    ),
                                ),
                                Text(
                                    caption = subTitle ?: getText("page.login.sub-title"),
                                    alignment = TextAlignment.Center,
                                ),
                            ),
                            crossAxisAlignment = center,
                            mainAxisAlignment = MainAxisAlignment.center
                        )
                    ),
                    Container(
                        alignment = Center,
                        child = PinWithKeyboard(
                            name = "pin",
                            hideText = true,
                            deleteText = getText("page.login.key.delete"),
                            maxLength = 6,
                            action = Action(
                                type = Command,
                                url = urlBuilder.build(submitUrl(phoneNumber, auth, returnUrl, returnToRoute))
                            )
                        )
                    )
                )
            )
        )
    ).toWidget()

    private fun submitUrl(phoneNumber: String, auth: Boolean, returnUrl: String?, returnToRoute: Boolean): String {
        val url =
            "commands/login?auth=$auth&return-to-route=$returnToRoute&phone=" + URLEncoder.encode(phoneNumber, "utf-8")
        return if (returnUrl == null)
            url
        else
            url + "&return-url=" + URLEncoder.encode(returnUrl, "utf-8")
    }
}
