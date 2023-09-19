package io.github.alice52

import common.swagger.annotation.EnableSwagger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableSwagger
@SpringBootApplication
open class HttpsApplication

fun main(vararg args: String) {
    runApplication<HttpsApplication>(*args)
}
