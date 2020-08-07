package io.craigmiller160.apitestprocessor

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.apitestprocessor.config.*
import io.craigmiller160.apitestprocessor.exception.BadConfigException
import io.craigmiller160.apitestprocessor.result.ApiResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.lang.RuntimeException
import java.util.*

class ApiTestProcessor (init: SetupConfig.() -> Unit) {

    private val setupConfig = SetupConfig()
    private var objectMapper: ObjectMapper
    private var mockMvc: MockMvc
    private var authConfig: AuthConfig

    init {
        setupConfig.init()
        objectMapper = setupConfig.objectMapper
        mockMvc = setupConfig.mockMvc
        authConfig = setupConfig.authConfig
    }

    companion object {
        private const val AUTH_HEADER = "Authorization"
    }

    fun call(init: ApiConfig.() -> Unit): ApiResult {
        val apiConfig = ApiConfig()
        apiConfig.init()
        val reqBuilder = buildRequest(apiConfig.req)

        val result = mockMvc.perform(reqBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andReturn()

        validateResponse(apiConfig.res, result)
        return ApiResult(result, objectMapper)
    }

    private fun validateResponse(responseConfig: ResponseConfig, result: MvcResult) {
        assertEquals(responseConfig.status, result.response.status, "Incorrect response status")
        responseConfig.headers.forEach { (key, value) ->
            assertEquals(value, result.response.getHeader(key), "Incorrect value for header $key")
        }
    }

    private fun buildRequest(requestConfig: RequestConfig): MockHttpServletRequestBuilder {
        var reqBuilder = when(requestConfig.method) {
            HttpMethod.GET -> MockMvcRequestBuilders.get(requestConfig.path)
            HttpMethod.POST -> MockMvcRequestBuilders.post(requestConfig.path)
            HttpMethod.PUT -> MockMvcRequestBuilders.put(requestConfig.path)
            HttpMethod.DELETE -> MockMvcRequestBuilders.delete(requestConfig.path)
            else -> throw RuntimeException("Invalid HTTP method: ${requestConfig.method}")
        }
        reqBuilder = reqBuilder.secure(setupConfig.isSecure)
                .contentType(requestConfig.contentType)
        reqBuilder = handleAuth(requestConfig, reqBuilder)

        val body = requestConfig.body
        reqBuilder = when (body) {
            is String -> reqBuilder.content(body)
            is Any -> reqBuilder.content(objectMapper.writeValueAsString(body))
            else -> reqBuilder
        }

        return reqBuilder
    }

    private fun handleAuth(requestConfig: RequestConfig, reqBuilder: MockHttpServletRequestBuilder): MockHttpServletRequestBuilder {
        if (requestConfig.doAuth) {
            return when (authConfig.type) {
                AuthType.BASIC -> {
                    val userName = authConfig.userName ?: throw BadConfigException("Missing user name for Basic Auth")
                    val password = authConfig.password ?: throw BadConfigException("Missing password for Basic Auth")
                    val authString = "$userName:$password"
                    val encoded = Base64.getEncoder().encodeToString(authString.toByteArray())
                    reqBuilder.header(AUTH_HEADER, "Basic $encoded")
                }
                AuthType.BEARER -> {
                    val token = authConfig.bearerToken ?: throw BadConfigException("Missing bearer token for Bearer Auth")
                    reqBuilder.header(AUTH_HEADER, "Bearer $token")
                }
                AuthType.NONE -> reqBuilder
            }
        }
        return reqBuilder
    }

}
