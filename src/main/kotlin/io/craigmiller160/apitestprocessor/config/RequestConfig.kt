package io.craigmiller160.apitestprocessor.config

import io.craigmiller160.apitestprocessor.body.Body
import org.springframework.http.HttpMethod

class RequestConfig {
    var method: HttpMethod = HttpMethod.GET
    var path: String = ""
    var body: Body? = null
    internal var overrideAuth: AuthConfig? = null

    fun overrideAuth(init: AuthConfig.() -> Unit) {
        overrideAuth = AuthConfig()
        overrideAuth?.init()
    }
}
