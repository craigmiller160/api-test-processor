package io.craigmiller160.apitestprocessor.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.test.web.servlet.MockMvc
import java.util.function.Consumer

class SetupConfig {
    lateinit var mockMvc: MockMvc
    lateinit var objectMapper: ObjectMapper
    var isSecure = false

    internal val authConfig = AuthConfig()

    fun auth(init: AuthConfig.() -> Unit) {
        authConfig.init()
    }

    fun auth(init: Consumer<AuthConfig>) {
        auth { init.accept(this) }
    }

}
