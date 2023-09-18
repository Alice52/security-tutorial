package io.github.alice52.security.utils

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
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
    fun buildPublicByJks(publicKeyEncode: String, alg: String, format: String = "X.509"): PublicKey {

        if (format == "X.509") {
            val decodedBytes: ByteArray = Base64.getDecoder().decode(publicKeyEncode)
            val keySpec = X509EncodedKeySpec(decodedBytes)
            val keyFactory = KeyFactory.getInstance(alg)
            return keyFactory.generatePublic(keySpec)
        } else {
            TODO("NotImplement")
        }
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

