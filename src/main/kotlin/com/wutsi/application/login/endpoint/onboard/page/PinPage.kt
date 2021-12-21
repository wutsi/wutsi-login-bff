package com.wutsi.application.login.endpoint.onboard.page

import com.wutsi.application.login.endpoint.Theme
import com.wutsi.application.login.service.URLBuilder
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.PinWithKeyboard
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.enums.ActionType.Command
import com.wutsi.flutter.sdui.enums.Alignment.Center
import com.wutsi.flutter.sdui.enums.Alignment.TopCenter
import com.wutsi.flutter.sdui.enums.TextAlignment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pages/pin")
public class PinPage(
    private val urlBuilder: URLBuilder,
) : AbstractOnboardQuery() {
    @PostMapping
    fun index() = Container(
        alignment = Center,
        padding = 20.0,
        child = Column(
            children = listOf(
                Container(
                    alignment = Center,
                    child = Text(
                        caption = getText("page.pin.title"),
                        alignment = TextAlignment.Center,
                        size = Theme.LARGE_TEXT_SIZE,
                        color = Theme.PRIMARY_COLOR,
                        bold = true
                    )
                ),
                Container(
                    alignment = TopCenter,
                    padding = 10.0,
                    child = Text(
                        caption = getText("page.pin.sub-title"),
                        alignment = TextAlignment.Center,
                    )
                ),
                PinWithKeyboard(
                    name = "pin",
                    hideText = true,
                    maxLength = 6,
                    action = Action(
                        type = Command,
                        url = urlBuilder.build("commands/save-pin")
                    ),
                ),
            )
        )
    ).toWidget()
}
