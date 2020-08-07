package io.craigmiller160.apitestprocessor.config

class AuthConfig {
    var type: AuthType = AuthType.NONE
    var userName: String? = null
    var password: String? = null
    var bearerToken: String? = null
}
