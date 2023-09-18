package io.github.alice52.security.utils

import io.github.alice52.logger
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*

object RsaJwtUtil {

    fun generateJwt(id: String?, issuer: String?, aud: String?, subject: String?): String {
        val signatureAlgorithm: SignatureAlgorithm = SignatureAlgorithm.RS256
        val expDate = Date(System.currentTimeMillis() + 3600000)

        val builder =
            Jwts.builder().setId(id).setIssuedAt(Date()).setIssuer(issuer).setAudience(aud).setExpiration(expDate)
                .setSubject(subject).signWith(signatureAlgorithm, RsaUtil.getPrivateKey())
        return builder.compact()
    }

    @Deprecated(message = "在各自应用内使用jwt的自检验特性")
    fun parseByDeprecated(token: String): Boolean {
        val jws = Jwts.parserBuilder().setSigningKey(RsaUtil.getPublicKey()).build().parseClaimsJws(token)

        logger().info("jwt: {}", jws)

        return true
    }

}