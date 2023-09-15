package io.github.alice52

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class HttpsApplication

fun main(vararg args: String) {
    runApplication<HttpsApplication>(*args)
}
