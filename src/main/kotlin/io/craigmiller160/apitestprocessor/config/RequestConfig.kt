package io.craigmiller160.apitestprocessor.config

import org.springframework.http.HttpMethod

class RequestConfig {
    var method: HttpMethod = HttpMethod.GET
    var path: String = ""
    var body: Any? = null
    var doAuth: Boolean = true
}
