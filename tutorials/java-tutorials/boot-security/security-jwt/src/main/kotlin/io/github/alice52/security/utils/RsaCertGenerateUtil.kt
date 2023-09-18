package io.github.alice52.security.utils

import java.io.IOException
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

object RsaCertGenerateUtil {
    /**
     * 根据密文，生存 rsa公钥和私钥,并写入指定文件
     *
     * @param publicKeyFilename  公钥文件路径
     * @param privateKeyFilename 私钥文件路径
     * @param secret             生成密钥的密文
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Throws(Exception::class)
    fun generateKey(publicKeyFilename: String, privateKeyFilename: String, secret: String) {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        val secureRandom = SecureRandom(secret.toByteArray())
        keyPairGenerator.initialize(2048, secureRandom)
        val keyPair = keyPairGenerator.genKeyPair()
        // 获取公钥并写出
        val publicKeyBytes = keyPair.public.encoded
        FileUtil.writeFile(publicKeyFilename, publicKeyBytes)
        // 获取私钥并写出
        val privateKeyBytes = keyPair.private.encoded
        FileUtil.writeFile(privateKeyFilename, privateKeyBytes)
    }


}