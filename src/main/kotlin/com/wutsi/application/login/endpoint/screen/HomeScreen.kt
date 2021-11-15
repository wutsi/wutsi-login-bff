package com.wutsi.application.login.endpoint.screen

import com.wutsi.application.login.endpoint.AbstractQuery
import com.wutsi.application.login.endpoint.Page
import com.wutsi.application.login.endpoint.Theme
import com.wutsi.application.login.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment.Center
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
        @RequestParam(name = "title", required = false) title: String? = null,
        @RequestParam(name = "sub-title", required = false) subTitle: String? = null,
    ) = Screen(
        id = Page.HOME,
        safe = true,
        child = Container(
            alignment = Center,
            padding = 20.0,
            child = Column(
                children = listOf(
                    Container(
                        alignment = Center,
                        child = Text(
                            caption = title ?: getText("page.login.title"),
                            alignment = TextAlignment.Center,
                            size = Theme.HEADER_TEXT_SIZE,
                            bold = Theme.HEADER_BOLD
                        )
                    ),
                    Container(
                        alignment = Center,
                        padding = 10.0,
                        child = Text(
                            caption = subTitle ?: getText("page.login.sub-title"),
                            alignment = TextAlignment.Center,
                            size = Theme.LARGE_TEXT_SIZE,
                        )
                    ),
                    Container(
                        alignment = Center,
                        padding = 10.0,
                        child = PinWithKeyboard(
                            name = "pin",
                            hideText = true,
                            deleteText = getText("page.login.key.delete"),
                            maxLength = 6,
                            action = Action(
                                type = Command,
                                url = urlBuilder.build("commands/login?phone=" + URLEncoder.encode(phoneNumber, "utf-8"))
                            )
                        )
                    )
                )
            )
        )
    ).toWidget()
}
