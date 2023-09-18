//package io.github.alice52.security.utils
//
//import io.jsonwebtoken.Jwts
//import io.jsonwebtoken.SignatureAlgorithm
//import org.springframework.core.io.ClassPathResource
//import java.security.KeyPair
//import java.security.KeyStore
//import java.security.PrivateKey
//import java.security.PublicKey
//import java.security.cert.Certificate
//import java.security.interfaces.RSAPrivateKey
//import java.security.interfaces.RSAPublicKey
//import java.util.*
//
//
///**
// * @author alice52
// * @date 2023/9/18
// * @project boot-security
// */
//object RsaJwtUtilA {
//
//    private val rsaStoreSecret = "123456"
//    private val rsaKeySecret = "123456"
//
//    /**
//     * 将 jks 转换为密钥对
//     */
//    fun keyPair(): KeyPair {
//        val keystore = KeyStore.getInstance("PKCS12")
//        keystore.load(ClassPathResource("jwt_rsa.jks").inputStream, rsaStoreSecret.toCharArray())
//
//        val key = keystore.getKey("jwt_rsa", rsaKeySecret.toCharArray()) as PrivateKey
//        val cert: Certificate = keystore.getCertificate("jwt_rsa")
//
//        val publicKey: PublicKey = cert.getPublicKey()
//        return KeyPair(publicKey, key)
//    }
//
//    /**
//     * 通过 API(或直接将公钥下发) 获取公钥进行jwt验证
//     */
//    // @Deprecated("extra from jwt_rsa_public.pem.jks")
//    fun getPublicDeprecated(): RSAPublicKey {
//
//        return keyPair().public as RSAPublicKey
//    }
//
//
//    private fun generalPrivateKey(): RSAPrivateKey {
//
//        return keyPair().private as RSAPrivateKey
//    }
//
//    fun generateJwt(id: String?, issuer: String?, aud: String?, subject: String?): String {
//        val signatureAlgorithm: SignatureAlgorithm = SignatureAlgorithm.RS256
//
//        val header: MutableMap<String, Any> = HashMap()
//        header["alg"] = "RS256"
//        header["typ"] = "JWT"
//        val expDate = Date(System.currentTimeMillis() + 3600000)
//
//        val builder = Jwts.builder().setHeader(header).setId(id).setIssuedAt(Date()).setIssuer(issuer).setAudience(aud)
//            .setExpiration(expDate).setSubject(subject).signWith(signatureAlgorithm, RsaUtil.getPrivateKey("rsa.pri"))
//        return builder.compact()
//    }
//
//
//}