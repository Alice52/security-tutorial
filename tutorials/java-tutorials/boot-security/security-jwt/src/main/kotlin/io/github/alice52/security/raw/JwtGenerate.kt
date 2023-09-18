package io.github.alice52.security.raw

import cn.hutool.json.JSONObject
import io.github.alice52.logger
import java.security.NoSuchAlgorithmException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * @author alice52
 * @date 2023/9/18
 * @project boot-security
 */
class JwtGenerate {

    // constants
    companion object {
        val SECRET_KEY = "FREE_MASON"
        val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
        val ISSUER = "mason.metamug.net"
        val JWT_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}"
    }

    // jwt struct: base64(header).base64(payload).signature
    class RawJwt {
        lateinit var encodedHeader: String
        lateinit var payload: String
        lateinit var signature: String

        override fun toString(): String {
            return "$encodedHeader.$payload.$signature"
        }
    }


    fun generate(sub: String, aud: Array<String>, expires: Long): RawJwt {

        val epochSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

        val jwt = RawJwt()
        jwt.encodedHeader = encode(JSONObject(JWT_HEADER));

        val payload = JSONObject()
        payload.append("sub", sub)
        payload.append("aud", aud)
        payload.append("exp", epochSecond + expires)
        payload.append("iat", epochSecond)
        payload.append("iss", ISSUER)
        payload.append("jti", UUID.randomUUID().toString())
        jwt.payload = encode(payload);

        val signature = hmacSha256(jwt.encodedHeader + "." + jwt.payload, SECRET_KEY)
        jwt.signature = signature

        return jwt
    }

    /**
     * For verification
     *
     * @param token
     * @throws java.security.NoSuchAlgorithmException
     */
    @Throws(NoSuchAlgorithmException::class)
    fun validThenParse(token: String) {
        val parts = token.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        require(parts.size == 3) { "Invalid token format" }

        // validate signature
        if (parts[2] != hmacSha256(parts[0] + "." + parts[1], SECRET_KEY)) {
            throw RuntimeException("Invalid token");
        }

        // validate exp
        val payload = JSONObject(decode(parts[1]))
        val exp = payload.getBeanList("exp", String::class.java).get(0).toLong()
        if (exp < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
            throw RuntimeException("Expired token");
        }

        logger().info("jwt parse result: header: {}, payload: {}", decode(parts[0]), payload)
    }


    private fun encode(obj: JSONObject): String {
        return encode(obj.toString().encodeToByteArray())
    }

    private fun encode(bytes: ByteArray): String {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun decode(encodedString: String): String {
        return String(Base64.getUrlDecoder().decode(encodedString))
    }

    /**
     * Sign with HMAC SHA256 (HS256)
     *
     * @param data
     * @return
     * @throws Exception
     */
    private fun hmacSha256(data: String, secret: String): String {
        return try {
            //MessageDigest digest = MessageDigest.getInstance("SHA-256");
            val hash: ByteArray = secret.encodeToByteArray()
            val sha256Hmac: Mac = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(hash, "HmacSHA256")
            sha256Hmac.init(secretKey)
            val signedBytes: ByteArray = sha256Hmac.doFinal(data.encodeToByteArray())
            encode(signedBytes)
        } catch (ex: Exception) {
            logger().error(ex.toString())
            throw ex
        }
    }
}