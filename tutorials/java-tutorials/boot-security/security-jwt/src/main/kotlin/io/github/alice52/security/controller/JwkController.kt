package io.github.alice52.security.controller

import io.github.alice52.security.model.Jwk
import io.github.alice52.security.util.RsaJwtUtil
import io.github.alice52.security.util.RsaUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.interfaces.RSAPublicKey
import java.security.spec.AlgorithmParameterSpec
import java.util.*


/**
 * @author alice52
 * @date 2023/9/18
 * @project security-jwt
 */
@RestController
@RequestMapping("/auth")
class JwkController {

    @GetMapping("/jwk")
    fun jks(): Jwk {
        val pk = RsaUtil.getPublicKey() as RSAPublicKey
        return RsaUtil.parse2Jwk(pk)
    }

    @Deprecated("使用jwk构建公钥进行验证")
    @GetMapping("/check-token-by-jwk")
    fun validateByJwk(@RequestParam("token") token: String): Boolean {
        val jwk = Jwk(
            kty = "RSA",
            e = "AQAB",
            n = "AJPe1n5hIwVqR_BWJ1KNIIfg6H9PYGhfj1Hs1ACj1_X_rIUdJFloXl0b-j7iM9SKeZOpDlu0OR4JDG8Bq5Ngh0zW_Gzo4bs6TgTd3ly1HVK9ZRFYaCJIRWar0MNIrhwVoIUWBCZjh3CwqoHSwi-Q6dsj0yZYFr9VNQw7M6HfyQvSRYWOODWrRNeDX3aCbd29vD_AG-Hxu2YyuJSP5f79jJPSmVTFveV13HMJAKF1wrxAADNNdw3TP7azpFKCAtrhL9MmjUma5JSEBHwob94rL-9U9f8t_33fmA6330QdumWyKdcX56iIMMgJxCZjFESx9OUo5Llpa8gmvqIpF5XKaWM"
        )
        return RsaJwtUtil.parseByJwk(token, jwk)
    }


    data class PublicKey(
        val algorithm: String?,
        /* 编码后的公钥: 字节数组通常使用X.509标准的DER编码格式 */
        val encoded: ByteArray?,
        val params: AlgorithmParameterSpec?,
        val format: String?,
    )

    /**
     * @see RsaUtil#buildPublicByJks()
     */
    @Deprecated("直接将公钥的base64暴露出去")
    @GetMapping("/public-encoded")
    fun publishEncode(): PublicKey {
        val pk = RsaUtil.getPublicKey() as RSAPublicKey

        return PublicKey(
            pk.algorithm,
            pk.encoded,
            pk.params,
            pk.format
        )
    }

    @Deprecated("使用公钥进行验证")
    @GetMapping("/check-token-by-pblic-encode")
    fun validateByPublicEncode(@RequestParam("token") token: String): Boolean {
        return RsaJwtUtil.parseByPublicEncode(token)
    }
}