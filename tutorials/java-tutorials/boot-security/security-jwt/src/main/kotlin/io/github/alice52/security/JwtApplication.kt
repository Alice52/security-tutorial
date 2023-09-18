package io.github.alice52.security

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JwtApplication

fun main(args: Array<String>) {
    runApplication<JwtApplication>(*args)
}