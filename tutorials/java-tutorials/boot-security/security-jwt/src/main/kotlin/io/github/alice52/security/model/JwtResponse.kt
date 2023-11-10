package io.github.alice52.security.model

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws


/**
 * @author alice52
 * @date 2023/9/18
 * @project boot-security
 */
data class JwtResponse(
    val message: String, val status: Status, val exceptionType: String, val jwt: String, val jws: Jws<Claims>
) {
    enum class Status {
        SUCCESS, ERROR
    }
}

