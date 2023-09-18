package io.github.alice52.security.raw

import io.github.alice52.logger
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SigningKeyResolverAdapter
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.time.Instant
import java.util.*


/**
 * @author alice52
 * @date 2023/9/18
 * @project boot-security
 */
class JjwtGenerate4Sha256 {
    val secret: String = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E="
    val key: Key = Keys.hmacShaKeyFor(secret.encodeToByteArray())


    fun simpleGenerate(): String {

        val jws: String =
            Jwts.builder().setSubject("Joe").signWith(/*Keys.secretKeyFor(SignatureAlgorithm.HS256)*/key).compact()

        return jws;
    }

    fun generate(): String {
        val header = Jwts.header()
        header.put("aHeaderName", "aValue")

        val jws: String = Jwts.builder().setHeader(header).setIssuer("Stormpath").setSubject("msilverman")
            .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))
            .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L))).claim("app", "Micah Silverman")
            .claim("scope", "admins").signWith(key).compact();

        return jws;
    }

    fun validThenParse(rawToken: String) {
        val keyResolver = object : SigningKeyResolverAdapter() {
            override fun resolveSigningKeyBytes(header: JwsHeader<*>, claims: Claims): ByteArray {
                return Decoders.BASE64.decode(secret)
            }
        }

        val jws = Jwts.parserBuilder().setSigningKey(key)

            // .setSigningKeyResolver(keyResolver)
            .build().parseClaimsJws(rawToken)

        logger().info("jwt: {}", jws)
    }
}