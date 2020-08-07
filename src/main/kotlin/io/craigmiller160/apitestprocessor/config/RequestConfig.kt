package io.craigmiller160.apitestprocessor.config

import io.craigmiller160.apitestprocessor.body.Body
import org.springframework.http.HttpMethod

class RequestConfig {
    var method: HttpMethod = HttpMethod.GET
    var path: String = ""
    var contentType = "application/json"
    var body: Body? = null
    var doAuth: Boolean = true
}
