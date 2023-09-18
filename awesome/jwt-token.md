[toc]

## traditional solution

### session

1. User sends username and password to server
2. After the server passes the authentication, relevant data is saved in the current session (such as user role, login time, etc.)
3. The server returns a session_id to the user and writes the user's cookie
4. Each user subsequent request will pass the session_id back to the server via a cookie
5. The server receives the session_id, finds the data saved in the previous period, and knows the identity of the user

### disadvantage

1. scaling bad
2. Oriented to Stand-alone
3. 如果是服务器集群, 或者是跨域的服务导向架构, 就要求 session 数据共享, 每台服务器都能够读取 session
4. 如果 session 存储的节点挂了, 那么整个服务都会瘫痪, 体验相当不好, 风险也很高

## jwt

1. JWT 是一个`自包含`的访问令牌[带有声明和过期的受保护数据结构], 其实现方式是将用户信息存储在客户端, 服务端不进行保存
2. 作用: 用来在身份提供者和服务提供者间传递安全可靠的信息
3. struct: Header.Payload.Signature[Base64-URL]

   - Header: 令牌的类型 + 使用的签名算法(对前两部分进行签名: HS256 | RS256 等)
   - Payload: Claim 是 Payload 中的键值对(注册声明、公共声明和私有声明)

     1. 注册声明
        - iss(签发人)
        - exp(过期时间)
        - sub(主题): 它的所有人(可以存用户信息等)
        - aud(受众): 接收 jwt 的一方
        - nbf(生效时间)
        - iat(签发时间)
        - jti(编号): 主要用来作为一次性 token,从而回避重放攻击
     2. 公共声明: 任何与应用程序相关的有用信息
     3. 私有声明: any custom column

   - Signature: 对前两部分的签名(secret 相当于 password/slat), 防止数据篡改.

     1. _HS256 使用密钥生成固定的签名_: HS256 必须与任何想要验证 JWT(签名密钥需要需要共享私钥)
        ![avatar](/static/image/oauth/jwt-hs256.png)
     2. RS256 使用成非对称进行签名: 用私钥来签签名, 用公钥来验证签名(保证服务端签名者 + 不需要共享私钥)
        - 公钥加密, 私钥可以解密: 保证数据传输的安全
        - **私钥签名(数字摘要), 公钥可以验证签名**
          ![avatar](/static/image/oauth/jwt-rsa.png)

4. processor

   ![avatar](/static/image/oauth/jwt-processor.png)

   ![avatar](/static/image/oauth/jwt-workflow.png)

5. feature

   - 不可撤回: API 了解了密钥材料, 它就可以验证自包含的令牌, 而无需与发行者进行通信
   - JWT 默认是不加密, 可以用密钥再加密一次原始 Token
   - JWT 应该使用 HTTPS 协议传输减少盗用
   - JWT 去中心化的思想[Validate]
     - 资源收到第一个请求之后, 会去 id4 服务器获取公钥, 然后用公钥验证 token 是否合法
     - 如果合法进行后面的有效性验证
     - 有且只有第一个请求才会去 id4 服务器请求公钥
     - 后面的请求都会用第一次请求的公钥来验证
     - JWT 本身包含了认证信息, 任何人都可以获得该令牌的所有权限. JWT 的有效期应该设置得比较短. 对于一些比较重要的权限, 使用时应该再次对用户进行认证。

## reference Token: 不携带任何用户数据且可撤回

1. concept

   - 服务端会对 Token 进行持久化

2. work folow

   - 客户端请求资源端的时候, 资源端需要每次都去服务端通信去验证 Token 的合法性
   - 使用引用令牌时 IdentityServer 会将令牌的内容存储在数据存储中,
   - 并且只会将此令牌的唯一标识符发回给客户端
   - 接收此引用的 API 必须打开与 IdentityServer 的反向通道通信以验证令牌

   ![avatar](/static/image/oauth/reference-token.png)

## conclusion

1. JWT 在获取 token 后不需要再次返回 server, 需要知道 token 和 secret 就可以算出 signature, 与 token 中的 signature 进行比较, 一致说明用户合法且处于登录状态[内部会校验 time 等参数]
2. 密钥(secret)只有服务器才知道, 不能泄露给用户, 用于 signature 的生成
3. JWT 校验时, 只会比较比较 signature 和校验 payload 中 time 数据等信息

---

## reference

1. https://www.cnblogs.com/guolianyu/p/9872661.html
2. [jwt-自验证](https://blog.csdn.net/awodwde/article/details/113900779)
