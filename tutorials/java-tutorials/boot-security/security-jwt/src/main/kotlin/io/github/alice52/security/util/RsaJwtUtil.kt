package io.github.alice52.security.util

import io.github.alice52.logger
import io.github.alice52.security.model.Jwk
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

    fun parseByPublicEncode(token: String): Boolean {
        val jws = Jwts.parserBuilder().setSigningKey(RsaUtil.getPublicKey()).build().parseClaimsJws(token)
        logger().info("jwt: {}", jws)
        return true
    }

    fun parseByJwk(token: String, jwk: Jwk): Boolean {

        val jws = Jwts.parserBuilder().setSigningKey(RsaUtil.buildByJwk(jwk)).build().parseClaimsJws(token)
        logger().info("jwt: {}", jws)
        return true
    }

}