package io.github.alice52.security.raw

import io.github.alice52.security.util.RsaCertGenerateUtil
import io.github.alice52.security.util.RsaUtil
import org.junit.Test


/**
 * @author alice52
 * @date 2023/9/18
 * @project boot-security
 */
class RsaUtilTest {


    /**
     * 测试RSAUtils工具类的使用
     *
     *
     * @param args
     */
    @Test
    fun testRsaUtil() {
        // rsa.pub文件名随意，例如：rsa.pub、rsa.io、pub.opp、rsapub.tyrf、rsa.txt、、、、
        val pubKeyPath = "rsa.pub"
        val priKeyPath = "rsa.pri"

        // 明文
        val secret = "sc@Login(Auth}*^31)"
        RsaCertGenerateUtil.generateKey(pubKeyPath, priKeyPath, secret)
        println("ok")

        val publicKey = RsaUtil.getPublicKey(pubKeyPath)
        System.out.println("公钥:" + publicKey);

        val privateKey = RsaUtil.getPrivateKey(priKeyPath)
        System.out.println("私钥：" + privateKey);
    }
}