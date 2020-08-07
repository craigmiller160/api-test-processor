package io.craigmiller160.apitestprocessor.config

class ApiConfig {
    internal var req = RequestConfig()
    internal var res = ResponseConfig()

    fun request(init: RequestConfig.() -> Unit) {
        val request = RequestConfig()
        request.init()
        req = request
    }

    fun response(init: ResponseConfig.() -> Unit) {
        val response = ResponseConfig()
        response.init()
        res = response
    }
}
