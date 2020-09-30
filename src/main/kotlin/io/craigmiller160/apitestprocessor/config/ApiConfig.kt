package io.craigmiller160.apitestprocessor.config

import java.util.function.Consumer

class ApiConfig {
    internal var req = RequestConfig()
    internal var res = ResponseConfig()

    fun request(init: Consumer<RequestConfig>) {
        request { init.accept(this) }
    }

    fun request(init: RequestConfig.() -> Unit) {
        val request = RequestConfig()
        request.init()
        req = request
    }

    fun response(init: Consumer<ResponseConfig>) {
        response { init.accept(this) }
    }

    fun response(init: ResponseConfig.() -> Unit) {
        val response = ResponseConfig()
        response.init()
        res = response
    }
}
