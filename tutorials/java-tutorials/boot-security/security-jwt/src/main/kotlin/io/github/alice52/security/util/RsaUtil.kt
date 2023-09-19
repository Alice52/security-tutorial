package io.github.alice52.security.util

import io.github.alice52.security.model.Jwk
import java.math.BigInteger
import java.security.*
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*


/**
 * @author alice52
 * @date 2023/9/18
 * @project boot-security
 */
object RsaUtil {
    val RSA: String = "RSA"
    val rsaPub: String = "rsa.pub"
    val rsaPri: String = "rsa.pri"

    /**
     * 从文件中读取公钥
     *
     * @param filename 公钥保存路径，相对于classpath
     * @return 公钥对象
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getPublicKey(filename: String = rsaPub): PublicKey {
        val bytes = FileUtil.readFile(filename)
        return getPublicKey(bytes)
    }

    @Throws(Exception::class)
    fun buildByPublicEncode(publicKeyEncode: String, alg: String, format: String = "X.509"): PublicKey {

        if (format == "X.509") {
            val decodedBytes: ByteArray = Base64.getDecoder().decode(publicKeyEncode)
            val keySpec = X509EncodedKeySpec(decodedBytes)
            val keyFactory = KeyFactory.getInstance(alg)
            return keyFactory.generatePublic(keySpec)
        } else {
            TODO("NotImplement")
        }
    }

    /**
     * @see #parse2Jwk()
     */
    fun buildByJwk(jwk: Jwk): PublicKey {
        val modulus = BigInteger(
            1, Base64.getUrlDecoder().decode(jwk.n)
        )
        val exponent = BigInteger(1, Base64.getUrlDecoder().decode(jwk.e))

        // 构建RSAPublicKeySpec对象
        val keyFactory = KeyFactory.getInstance(RSA)
        val publicKey = keyFactory.generatePublic(RSAPublicKeySpec(modulus, exponent))

        // 确认PublicKey是RSAPublicKey类型
        return if (publicKey is RSAPublicKey) {
            publicKey
        } else {
            throw IllegalArgumentException("JWK JSON does not represent an RSA public key")
        }
    }

    /**
     * @see #buildByJwk()
     */
    fun parse2Jwk(pk: RSAPublicKey): Jwk {
        // 获取公钥的模数和指数
        val modulus: BigInteger = pk.getModulus()
        val exponent: BigInteger = pk.getPublicExponent()

        // 将模数和指数转换为Base64URL字符串
        val encodedModulus: String = base64UrlEncode(modulus.toByteArray())
        val encodedExponent: String = base64UrlEncode(exponent.toByteArray())

        // 构建JWK对象
        return Jwk(
            kty = "RSA", e = encodedExponent, n = encodedModulus
        )
    }

    private fun base64UrlEncode(bytes: ByteArray): String {
        val base64: String = Base64.getUrlEncoder().encodeToString(bytes)
        // 移除末尾的等号，并替换字符“+”和“/”为字符“-”和“_”
        return base64.replace("=+$".toRegex(), "").replace('+', '-').replace('/', '_')
    }

    private fun getPublicKey(bytes: ByteArray?): PublicKey {
        val spec = X509EncodedKeySpec(bytes)
        val factory: KeyFactory = KeyFactory.getInstance(RSA)
        return factory.generatePublic(spec)
    }

    /**
     * 从文件中读取密钥
     *
     * @param filename 私钥保存路径，相对于classpath
     * @return 私钥对象
     * @throws Exception
     */
    fun getPrivateKey(filename: String = rsaPri): PrivateKey {
        val bytes = FileUtil.readFile(filename)
        return getPrivateKey(bytes)
    }

    private fun getPrivateKey(bytes: ByteArray?): PrivateKey {
        val spec = PKCS8EncodedKeySpec(bytes)
        val factory: KeyFactory = KeyFactory.getInstance(RSA)
        return factory.generatePrivate(spec)
    }
}

