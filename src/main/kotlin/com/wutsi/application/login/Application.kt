package com.wutsi.application.login

import com.wutsi.application.shared.WutsiBffApplication
import com.wutsi.platform.core.WutsiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@WutsiApplication
@WutsiBffApplication
@SpringBootApplication
@EnableScheduling
public class Application

public fun main(vararg args: String) {
    org.springframework.boot.runApplication<Application>(*args)
}
