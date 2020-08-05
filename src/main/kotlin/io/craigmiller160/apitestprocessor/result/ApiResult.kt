package io.craigmiller160.apitestprocessor.result

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.test.web.servlet.MvcResult

class ApiResult (
        val mvcResult: MvcResult,
        private val objectMapper: ObjectMapper
) {

    val content = mvcResult.response.contentAsString
    val response = mvcResult.response

    fun <T> convert(type: Class<T>): T {
        return objectMapper.readValue(content, type)
    }

}
