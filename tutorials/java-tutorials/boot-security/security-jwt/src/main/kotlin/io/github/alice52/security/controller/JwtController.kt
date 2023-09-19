package io.github.alice52.security.controller

import io.github.alice52.security.util.RsaJwtUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author alice52
 * @date 2023/9/18
 * @project boot-security
 */
@RestController
@RequestMapping("/auth")
class JwtController {


    @GetMapping("/token")
    fun token(): String {
        return RsaJwtUtil.generateJwt(id = "id", issuer = "issuer", aud = "aud", subject = "subject")
    }

}