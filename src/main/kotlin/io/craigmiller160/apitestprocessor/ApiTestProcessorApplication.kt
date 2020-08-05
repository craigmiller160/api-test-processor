package io.craigmiller160.apitestprocessor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiTestProcessorApplication

fun main(args: Array<String>) {
    runApplication<ApiTestProcessorApplication>(*args)
}
