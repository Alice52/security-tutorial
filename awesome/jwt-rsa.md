## rsa

1. [RsaCertGenerateUtil.kt](src%2Fmain%2Fkotlin%2Fio%2Fgithub%2Falice52%2Fsecurity%2Futils%2FRsaCertGenerateUtil.kt)

   - 生成公钥和私钥: 使用随机值确保安全和增加破解难度
   - 私钥转换为 PrivateKey 对 Jwt 进行签名(签名值可以被看作是对原始数据的摘要)
   - 公钥转换为 PublicKey 对 Jwt 进行签名验证(未修改): 数学逻辑保证可以使用公钥验证私钥的签名

2. 构建公钥的 PublicKey: `通过公钥的 /jks 暴露公钥` || `直接将公钥文件下发`

   ```kotlin
   // 直接将公钥文件下发
   fun getPublicKey(filename: String = rsaPub): PublicKey {
       val bytes = FileUtil.readFile(filename)
       return getPublicKey(bytes)
   }

   // 通过公钥的 /jks 暴露公钥
   fun buildPublicByJks(publicKeyEncode: String, alg: String, format: String = "X.509"): PublicKey {

       if (format == "X.509") {
           val decodedBytes: ByteArray = Base64.getDecoder().decode(publicKeyEncode)
           return getPublicKey(decodedBytes)
       } else {
           TODO("NotImplement")
       }
   }

   private fun getPublicKey(bytes: ByteArray?): PublicKey {
       val spec = X509EncodedKeySpec(bytes)
       val factory: KeyFactory = KeyFactory.getInstance(RSA)
       return factory.generatePublic(spec)
   }
   ```

3. jwt 生成

   ```kotlin
   fun generateJwt(id: String?, issuer: String?, aud: String?, subject: String?): String {
       val builder =
           Jwts.builder().setId(id)
               .xx
               .signWith( SignatureAlgorithm.RS256, RsaUtil.getPrivateKey())
       return builder.compact()
   }
   ```

4. jwt 校验

   ```kotlin
   fun parse(token: String): Boolean {
       val jws = Jwts
           .parserBuilder()
           .setSigningKey(RsaUtil.getPublicKey())
           .build()
           .parseClaimsJws(token)
       logger().info("jwt: {}", jws)

       return true
   }
   ```

## issue

1. 怎么使用一下命令创建公钥私钥

   ```shell
   keytool -genkeypair -storetype PKCS12 -alias jwt_rsa -keyalg RSA -keypass 123456 -keystore jwt_rsa.jks -storepass 123456
   keytool -export -alias jwt_rsa -keystore jwt_rsa.jks -file jwt_rsa_public.jks
   keytool -list -rfc --keystore jwt_rsa.jks | openssl x509 -inform pem -pubkey
   ```

## reference

1. https://blog.csdn.net/yy339452689/article/details/127107164
2. https://blog.csdn.net/weixin_45262834/article/details/114951571
3. https://blog.csdn.net/weixin_41245089/article/details/88185206
4. https://blog.csdn.net/qq_38225558/article/details/125262117
5. https://blog.csdn.net/tonydz0523/article/details/106676135
