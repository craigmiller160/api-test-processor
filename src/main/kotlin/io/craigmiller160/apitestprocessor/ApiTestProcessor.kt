package io.craigmiller160.apitestprocessor

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.apitestprocessor.config.ApiConfig
import io.craigmiller160.apitestprocessor.result.ApiResult
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
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

        var reqBuilder = when(apiConfig.req.method) {
            HttpMethod.GET -> MockMvcRequestBuilders.get(apiConfig.req.path)
            HttpMethod.POST -> MockMvcRequestBuilders.post(apiConfig.req.path)
            HttpMethod.PUT -> MockMvcRequestBuilders.put(apiConfig.req.path)
            HttpMethod.DELETE -> MockMvcRequestBuilders.delete(apiConfig.req.path)
            else -> throw RuntimeException("Invalid HTTP method: ${apiConfig.req.method}")
        }
        reqBuilder = reqBuilder.secure(true)
        if (apiConfig.req.doAuth && authToken != null) {
            reqBuilder = reqBuilder.header("Authorization", "Bearer $authToken")
        }

        if (apiConfig.req.body != null) {
            reqBuilder = reqBuilder.contentType("application/json")
                    .content(objectMapper.writeValueAsString(apiConfig.req.body))
        }

        val result = mockMvc.perform(reqBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().`is`(apiConfig.res.status))
                .andReturn()
        return ApiResult(result, objectMapper)
    }

}
