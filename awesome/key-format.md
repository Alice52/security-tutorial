- 公钥(十六进制): `30820122300d06092a864886f70d01010105000382010f003082010a02820101009c4a8b8b6b7`

## 公钥格式

1. spki: ASN.1 编码的公钥表示方法(DER)

   ```js
   // 公钥被编码为DER（Distinguished Encoding Rules）序列, 并嵌套在SubjectPublicKeyInfo结构中
   30820122300d06092a864886f70d01010105000382010f003082010a02820101009c4a8b8b6b7
   ```

2. pkcs#1: 公钥密码学标准, 定义了 RSA 加密算法中的密钥格式和操作

   ```shell
   # 该公钥被包含在RSA公钥结构中, 该结构包括模数（n）和指数（e）
   -----BEGIN RSA PUBLIC KEY-----
   MIIBCgKCAQEAmsRo3nToOg7Fm1FqD2sQZEm2GnBv9...
   ...
   -----END RSA PUBLIC KEY-----
   ```

3. x.509 certificate: 是一种 PKI 标准, 用于描述数字证书的格式和内容

   ```shell
   # 公钥被包含在数字证书结构中, 该结构还包括证书主题、颁发者、有效期等信息
   -----BEGIN CERTIFICATE-----
   MIIDzDCCArSgAwIBAgIJAOtnP...
   ...
   ...
   -----END CERTIFICATE-----
   ```

4. pem: 是一种基于文本的数据格式并使用 Base64 编码进行表示

   ```shell
   -----BEGIN PUBLIC KEY-----
   MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmsRo3nToOg7Fm1FqD2sQ
   w0xvoUxmtvnitQ/5t6LOKptuQk+3W30OmhHQVghhPqrbGYc34l5dqRiHdVzBEXdT
   ...
   -----END PUBLIC KEY-----
   ```

5. jwk string format: 是一种用于描述密钥的 JSON 格式

   - "kty"(Key Type): 表示密钥的类型, 可能的值包括 "RSA"、"EC"、"oct" 等
   - "alg"(Algorithm): 表示密钥所使用的算法
   - "use"(Key Use): 表示密钥的用途, 例如 "enc" 表示用于加密, "sig" 表示用于签名
   - "kid"(Key ID): 表示密钥的唯一标识符
   - "x5u"(X.509 URL): 表示与密钥相关联的 X.509 证书 URL
   - "x5c"(X.509 Certificate Chain): 表示与密钥相关联的 X.509 证书链
   - "x5t"(X.509 Certificate SHA-1 Thumbprint): 表示与密钥相关联的 X.509 证书的 SHA-1 摘要
   - "x5t#S256"(X.509 Certificate SHA-256 Thumbprint): 表示与密钥相关联的 X.509 证书的 SHA-256 摘要
   - 其他表示密钥参数的属性, 如 "n"(modulus)、"e"(exponent)等(这些属性依赖于密钥类型)

   ```json
   {
     "kty": "RSA",
     "alg": "RS256",
     "use": "sig",
     "kid": "12345",
     "n": "q62JQZSdFV...",
     "e": "AQAB"
   }
   ```

6. 常见公钥的后缀名

   - PEM 格式的公钥：
     1. .pem
     2. .pub
   - DER 格式的公钥：.der
   - SPKI 格式的公钥: .spki
   - PKCS#1 格式的公钥：
     1. .rsa
     2. .pem
     3. .pub
   - crt/cer

## 私钥格式

1. pkcs#1: 和上面一样
2. jwk string format: 和上面一样
3. pkcs#8

   ```shell
   -----BEGIN PRIVATE KEY-----
   MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDH
   ....
   -----END PRIVATE KEY-----
   ```

4. PKCS#12

   ```shell
   -----BEGIN PKCS12-----
   MIIE6zCCA9OgAwIBAgIQAUbUq.....................
   ...... (certificate, key, and other data) .......
   ..................................................
   -----END PKCS12-----
   ```

5. 常见公钥的后缀名

   - PEM 格式的私钥：
     1. .pem
     2. .pub
   - DER 格式的公钥：.der
   - PKCS#8/12 格式的私钥：
     1. .p8
     2. .pem
     3. .key
     4. .pfx
     5. .p12
   - OpenSSH 格式的私钥：
     1. .ssh
     2. .id_rsa
