package io.github.alice52.security.controller

import io.github.alice52.security.util.RsaJwtUtil
import io.github.alice52.security.util.RsaUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.interfaces.RSAPublicKey
import java.security.spec.AlgorithmParameterSpec

/**
 * @author alice52
 * @date 2023/9/18
 * @project security-jwt
 */
@RestController
@RequestMapping("/auth")
class JksController {

    data class Jks(
        val algorithm: String?,
        val encoded: ByteArray?,
        val params: AlgorithmParameterSpec?,
        val format: String?,
    )

    @GetMapping("/jks")
    fun jks(): Jks {
        val publicJks = RsaUtil.getPublicKey() as RSAPublicKey

        return Jks(
            publicJks.algorithm,
            publicJks.encoded,
            publicJks.params,
            publicJks.format
        )
    }

    @Deprecated("在各自服务内使用Jwt的自验证")
    @GetMapping("/check-token-v0")
    fun token(@RequestParam("token") token: String): Boolean {
        return RsaJwtUtil.parseByDeprecated(token)
    }
}