package io.craigmiller160.apitestprocessor

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.apitestprocessor.config.ApiConfig
import io.craigmiller160.apitestprocessor.config.RequestConfig
import io.craigmiller160.apitestprocessor.config.ResponseConfig
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

class ApiTestProcessor (
        private val mockMvc: MockMvc,
        private val objectMapper: ObjectMapper,
        private val isSecure: Boolean = false,
        private val authToken: String? = null
) {

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
        reqBuilder = reqBuilder.secure(true)
        if (requestConfig.doAuth && authToken != null) {
            reqBuilder = reqBuilder.header("Authorization", "Bearer $authToken")
        }

        if (requestConfig.body != null) {
            reqBuilder = reqBuilder.contentType("application/json")
                    .content(objectMapper.writeValueAsString(requestConfig.body))
        }

        return reqBuilder
    }

}
