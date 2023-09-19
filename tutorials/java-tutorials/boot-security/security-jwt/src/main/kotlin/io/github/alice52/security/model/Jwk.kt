package io.github.alice52.security.model

/**
 * 这个由 根据 RSAPublicKey 产生<br/>
 * @see RsaUtil#buildByJwk
 * 且可以根据 Jwk 构建 RSAPublicKey 用于签名验证 <br/>
 * @see RsaUtil#buildByJwk
 *
 * @author alice52
 * @date 2023/9/19
 * @project boot-security
 */
data class Jwk(
    val kty: String,
    val n: String, // encodedModulus
    val e: String, // encodedExponent
)