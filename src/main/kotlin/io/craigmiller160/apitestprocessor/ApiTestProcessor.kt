package io.craigmiller160.apitestprocessor

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.apitestprocessor.body.Form
import io.craigmiller160.apitestprocessor.body.Json
import io.craigmiller160.apitestprocessor.body.Text
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

        var resultActions = mockMvc.perform(reqBuilder)
        if (apiConfig.res.print) {
            resultActions = resultActions.andDo(MockMvcResultHandlers.print())
        }

        val result = resultActions.andReturn()

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
        var reqBuilder = handleRequest(requestConfig)
        reqBuilder = reqBuilder.secure(setupConfig.isSecure)
        reqBuilder = handleAuth(requestConfig, reqBuilder)
        reqBuilder = handleBody(requestConfig, reqBuilder)

        return reqBuilder
    }

    private fun handleRequest(requestConfig: RequestConfig): MockHttpServletRequestBuilder {
        return when(requestConfig.method) {
            HttpMethod.GET -> MockMvcRequestBuilders.get(requestConfig.path)
            HttpMethod.POST -> MockMvcRequestBuilders.post(requestConfig.path)
            HttpMethod.PUT -> MockMvcRequestBuilders.put(requestConfig.path)
            HttpMethod.DELETE -> MockMvcRequestBuilders.delete(requestConfig.path)
            else -> throw BadConfigException("Invalid HTTP method: ${requestConfig.method}")
        }
    }

    private fun handleBody(requestConfig: RequestConfig, reqBuilder: MockHttpServletRequestBuilder): MockHttpServletRequestBuilder {
        return requestConfig.body
                ?.let { body ->
                    when (body) {
                        is Json -> reqBuilder.contentType("application/json")
                                .content(objectMapper.writeValueAsString(body.value))
                        is Form -> reqBuilder.contentType("application/x-www-form-urlencoded")
                                .content(body.toUrlEncoded())
                        is Text -> reqBuilder.content("text/plain")
                                .content(body.body)
                        else -> throw BadConfigException("Unsupported Body implementation: ${body.javaClass}")
                    }
                }
                ?: reqBuilder
    }

    private fun handleAuth(requestConfig: RequestConfig, reqBuilder: MockHttpServletRequestBuilder): MockHttpServletRequestBuilder {
        val auth = requestConfig.overrideAuth ?: authConfig
        return when (auth.type) {
            AuthType.BASIC -> {
                val userName = auth.userName ?: throw BadConfigException("Missing user name for Basic Auth")
                val password = auth.password ?: throw BadConfigException("Missing password for Basic Auth")
                val authString = "$userName:$password"
                val encoded = Base64.getEncoder().encodeToString(authString.toByteArray())
                reqBuilder.header(AUTH_HEADER, "Basic $encoded")
            }
            AuthType.BEARER -> {
                val token = auth.bearerToken ?: throw BadConfigException("Missing bearer token for Bearer Auth")
                reqBuilder.header(AUTH_HEADER, "Bearer $token")
            }
            AuthType.NONE -> reqBuilder
        }
    }

}
