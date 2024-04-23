# v2

## oauth2.0

1. OAuth 2.0 依赖于 TLS/SSL 的链路加密技术（HTTPS）

   - 完全放弃了签名的方式(认证服务器再也不返回配置密钥): OAuth 2.0 是完全不同于 1.0 的且也是不兼容的
   - 1.0 及 ba/ka/hmac 等都是因为 HTTP 是明文传输防止篡改内容

2. Authentication & Authorization

   - Authentication(验证): **验证**尝试访问资源的用户、系统或实体的**身份的过程**, 确保用户或系统是他/她声称的人或物
     1. 基于密码的身份验证: 用户密码登录
     2. 两步验证(2FA): OTP
     3. 生物识别身份验证: 如指纹、虹膜模式或面部识别来验证身份
     4. 证书的身份验证: 由可信机构颁发的数字证书用于验证用户或系统的身份
   - Authorization(鉴权|授权): **授权**已经身份验证的用户或系统访问权限和权限的过程, 确保用户根据其角色或职责具有适当的权限
     1. 基于角色的访问控制(RBAC): 用户被分配特定角色, 权限与这些角色相关联. 访问基于用户的角色和相关权限的授权
     2. 基于属性的访问控制(ABAC): 访问决策基于分配给用户、资源和环境条件的一组属性
     3. 强制访问控制(MAC): 访问决策基于预定义的安全标签和中央管理机构建立的规则
     4. 自主访问控制(DAC): 资源的所有者有权自行控制访问, 指定个人用户或群组的权限

3. cons

   - **不用担心内容篡改**
   - 方便的接入第三方登录
   - 更细粒度的权限验证
   - 实施代码量小 | 维护工作减少

4. pros

   - 必须是 https
   - 学习成本比较高
   - 不是一个严格的标准协议

## concept

1. mandatory_scope: 指定在进行 OAuth 2.0 认证时, 客户端请求的访问令牌必须包含的作用域

   - 要求客户端在请求访问令牌时必须包含指定的作用域, 否则授权服务器将拒绝发放访问令牌
   - 有助于加强安全性, 确保客户端只能获得其被授权的权限范围内的访问令牌
   - 如 mandatory_scope 被配置为 read: 那么客户端在请求访问令牌时必须包含 read 作用域, 否则请求将被拒绝

2. scopes: 一种权限的概念, 用于限定客户端对资源的访问权限(read/write/xxx)
3. provision_key

   - 管理 API 的访问凭证之一, 通常是一个长字符串, 类似于一个 API 密钥
   - 它用于在请求管理 API 时进行身份验证和授权

4. PKCE
5. 授权模式: **Authorization Grants**

   - 用于在客户端和资源所有者之间进行安全的授权流程

6. 授权模式-authorization_code: 授权码模式

   - 客户端将用户重定向到授权服务器, 并请求授权(为了获取授权码): `:8443/demo/oauth2/authorize`
   - **用户登录**并授权客户端访问其受保护的资源
   - 授权服务器将用户重定向回客户端, 并提供一个授权码(code)
   - 客户端使用授权码向授权服务器请求访问令牌(token): `:8443/demo/oauth2/token?code=xx`
   - 授权服务器验证授权码, 并颁发访问令牌给客户端
   - 使用场景: [社交登录-flow](https://raw.githubusercontent.com/alice52/diagrams/master/diagrams/others/oauth/02.social-auth.svg)

   - [kong sample](https://blog.csdn.net/zz18435842675/article/details/120535528)

     1. 前置条件: 创建 service/route/plugin(Provision Key)/consumer(Oauth2 Credential)
     2. 调用 `http://localhost:8000/demo/xxx` 返回 400/401
     3. 调用`:8443/demo/oauth2/authorize`获取授权码(code)

        ```shell
        curl -X POST \
         --url "https://localhost:8443/demo/oauth2/authorize" \
         --data "response_type=code" \
         --data "scope=email" \
         --data "client_id=oauth2-demo-client-id" \
         --data "provision_key=oauth2-demo-provision-key" \
         --data "authenticated_userid=xxx" \  #IMPORTANT
         --insecure
        ```

        ```json
        {
          "redirect_uri": "http://localhost:8000/demo?code=g4JbD36VtnHuY9xDwdnnfqVH0PxCVAI2"
        }
        ```

     4. 使用授权码请求 token

        ```shell
        curl -X POST \
           --url "https://localhost:8443/demo/oauth2/token" \
           --data "grant_type=authorization_code" \
           --data "client_id=oauth2-demo-client-id" \
           --data "client_secret=oauth2-demo-client-secret"\
           --data "code=VhwdscbsZ6SLgsxEycXPtfDqCSyUxQzn" \
           --insecure
        ```

        ```json
        {
          "access_token": "bDC1gE7go3BlBhSuSXIYtRyStpMk2vJ3",
          "refresh_token": "1Q8rdIrjwKxoq3ESdus3hHU3lP8e4RHO",
          "expires_in": 7200,
          "token_type": "bearer"
        }
        ```

     5. 调用 `http://localhost:8000/demo/xxx --header 'Authorization: Bearer bDC1gE7go3BlBhSuSXIYtRyStpMk2vJx'` 返回正常
     6. 使用 refresh token 换 access token

        ```shell
        curl -X POST \
           --url "https://localhost:8443/demo/oauth2/token" \
           --data "grant_type=refresh_token" \
           --data "client_id=oauth2-demo-client-id" \
           --data "client_secret=oauth2-demo-client-secret" \
           --data "refresh_token=qHHk9BPbAXolkeCYyxft736HPhC5RDa3" \
           --insecure
        ```

        ```json
        {
          "token_type": "bearer",
          "expires_in": 7200,
          "access_token": "j2n6HwWsFjMKsJfWaZSyMhpFf1RpyX1g",
          "refresh_token": "aNSB2lXogOGCiVKOIoAPg0fxngt0VMeD"
        }
        ```

     7. 使用 authorization code grant 可以使用 PKCE 加强安全性: [code_verifier -> code_challenge](https://tonyxu-io.github.io/pkce-generator/)

        ```shell
        curl -X POST \
           --url "https://localhost:8443/demo/oauth2/authorize" \
           --data "response_type=code" \
           --data "scope=email address" \
           --data "client_id=oauth2-demo-client-id" \
           --data "provision_key=oauth2-demo-provision-key" \
           --data "authenticated_userid=authenticated_tester" \
           --data "code_challenge=nVFqpBvGXtATi0hhNnNuWE5PZNRQTNGR95DJZNcXEaU" \
           --data "code_challenge_method=S256" \
           --insecure

        curl -X POST \
           --url "https://localhost:8443/demo/oauth2/token" \
           --data "grant_type=authorization_code" \
           --data "client_id=oauth2-demo-client-id" \
           --data "client_secret=oauth2-demo-client-secret" \
           --data "code_verifier=8FK~B.3ERQPsn4xoSo.7pkmxc6wEiFabpqooHnFJKyyT3ZI41jh9DML0TA7UTVTYrxhUtsNfcOp9RcVhyKR~2GdWCFlv00WKFJ1ha_acuzeuyFYDI1.j4nJ3epQUmc0w" \
           --data "code=hyQvETmCU7fH4WUEBWSt7Knja5GBqDS4" \
           --insecure
        ```

7. _授权模式-implicit_grant: 隐式授权模式_(尽量不使用的 grant)

   - 客户端将用户重定向到授权服务器, 并请求授权
   - **用户登录**并授权客户端访问其受保护的资源
   - 授权服务器将用户重定向回客户端, 并直接提供访问令牌给客户端
   - 此模式将访问令牌直接传递给客户端, 而不是间接传递授权码

     1. 因此在**安全**性方面比授权码模式略有不足
     2. 通常用于一些**较低风险**的应用场景: 如单页应用或移动应用
     3. Implicit grant 应该只用来做身份的认证而不应该返回通行码用来使用 API: **callback 地址会在网络中传输就不安全了**

   - kong sample: 将 response_type 改为 token 则可以直接获取到 access_token

     ```json
     {
       "redirect_uri": "http://localhost:8000/demo#access_token=Q03uYGH46LRMJQA9sTqYonRvUGTwKqZg&expires_in=7200&token_type=bearer"
     }
     ```

8. 授权模式-client_credentials: 客户端凭据模式

   - 客户端向授权服务器提供其自身的凭据: 客户端 ID 和客户端密钥
   - 授权服务器验证客户端凭据, 并根据验证结果颁发访问令牌给客户端
   - 此模式通常用于**客户端需要直接访问其自身拥有的资源而不涉及用户**的情况

     1. 机器对机器通信: 因此认证服务器只会返回通行码而不会返回刷新码。每次通行码过期之后都需要重新请求新的
     2. 客户端自身的资源访问

   - kong sample: 不需要第一部获取 code, 可以直接获取到 access_token

     ```shell
     curl -X POST \
        --url "https://localhost:8443/demo/oauth2/token" \
        --data "grant_type=client_credentials" \
        --data "scope=email address" \
        --data "client_id=oauth2-demo-client-id" \
        --data "client_secret=oauth2-demo-client-secret" \
        --data "provision_key=oauth2-demo-provision-key" \
        --insecure
     ```

     ```json
     {
       "token_type": "bearer",
       "access_token": "7mKwrytPCZEgTjbr40rV5L0dk2Zykota",
       "expires_in": 7200
     }
     ```

9. 授权模式-password_grant: 密码模式

- 用户将其用户名和密码直接提供给客户端
- 客户端使用用户提供的凭据直接向授权服务器请求访问令牌
- 授权服务器验证用户凭据, 并根据验证结果颁发访问令牌给客户端
- 此模式通常用于**受信任**的应用程序, 如原生移动应用或受信任的**第一方**应用

- kong sample: 不需要第一步获取 code, 可以直接获取到 access_token

  ```shell
  # _用户需要在Kong的前面添加用户身份认证并且提供authenticated_userid给Kong来颁发通行码和刷新码给用户_
  curl -X POST \
     --url "https://localhost:8443/demo/oauth2/token" \
     --data "grant_type=password" \
     --data "scope=email address" \
     --data "client_id=oauth2-demo-client-id" \
     --data "client_secret=oauth2-demo-client-secret" \
     --data "provision_key=oauth2-demo-provision-key" \
     --data "authenticated_userid=xxx" \
     --insecure
  ```

---

## session

### traditional C/S

1. Introduction:
   - authenticating with the server using the resource owner's credentials
   - the resource owner shares its credentials with the third party
2. feature
   - Third-party applications are required to store the resource owner's credentials for future use, typically a password in clear-text.
   - Servers are required to support password authentication, despite the security weaknesses inherent in passwords.
   - Third-party applications gain overly broad access, and cannot limit duration or subset resources
   - Resource owners cannot revoke access individual, but can revoke access to all applications by changing password
   - Compromise of any third-party application results in compromise of the end-user's password and all of the data protected by that password.

### OAuth

#### Introduction

1. Introduce authorization layer and separating the client and owner
2. the client requests access to resources controlled by the resource owner and hosted by the resource server
3. issued a different set of credentials than those of the resource owner.
4. the client obtains an access token `denoting a specific scope, lifetime, and other access attributes`
5. Access tokens are issued to third-party clients by an authorization server with the approval of the resource owner.
6. The client uses the access token to access the protected resources hosted by the resource server

##### 1.1 role: four

1. resource owner: An entity capable of granting access to a protected resource.
2. resource server: The server hosting the protected resources, capable of accepting and responding to protected resource requests using access tokens.
3. client: use access token and request protected resource requests on behalf of the resource owner and with its authorization, including server, a desktop, or other devices
4. authorization server: The server issuing access tokens to the client after successfully authenticating the resource owner and obtaining authorization

##### 1.2 Protocol Flow

1. flow

   ```java
   +--------+                               +---------------+
   |        |--(A)- Authorization Request ->|   Resource    |
   |        |                               |     Owner     |
   |        |<-(B)-- Authorization Grant ---|               |
   |        |                               +---------------+
   |        |
   |        |                               +---------------+
   |        |--(C)-- Authorization Grant -->| Authorization |
   | Client |                               |     Server    |
   |        |<-(D)----- Access Token -------|               |
   |        |                               +---------------+
   |        |
   |        |                               +---------------+
   |        |--(E)----- Access Token ------>|    Resource   |
   |        |                               |     Server    |
   |        |<-(F)--- Protected Resource ---|               |
   +--------+                               +---------------+
   ```

   - A: The client requests authorization from the resource owner. The authorization request can be made directly to the resource owner (as shown), or preferably indirectly via the authorization server as an intermediary.
   - B: four grant types and extension grant type. The authorization grant type depends on the method used by the client to request authorization and the types supported by the authorization server.
   - C: the client get access token by using `Authorization Grant`
   - D: issues an access token.
   - E: client use access token to request protected resource
   - F: success request

2. Authorization Grant type

   - authorization code: get from authorization server

     ```json
     client --> resource owner --> Authorization server --Authorization Code-->  resource owner --> client
     ```

   - implicit: simplified
   - resource owner password credentials: there is a high degree of trust
   - client credentials: scope is limited to the protected resources

3. Access tokens

   - This abstraction enables issuing access tokens more restrictive than the authorization grant used to obtain them

4. Refresh tokens

   - issue by authorization server
   - access tokens may have a shorter lifetime and fewer permissions than authorized by the resource owner
   - Issuing a refresh token is optional at the discretion of the authorization server
   - flow

   ```java
   +--------+                                           +---------------+
   |        |--(0)----- Authorization Request --------> |   Resource    |
   |        |                                           |     Owner     |
   |        |<-(0)----- Authorization Grant ----------  |               |
   |        |                                           +---------------+
   |        |
   |        |
   |        |                                           +---------------+
   |        |                                           |               |
   |        |--(A)------- Authorization Grant --------->|               |
   |        |                                           |               |
   |        |<-(B)----------- Access Token -------------|               |
   |        |               & Refresh Token             |               |
   |        |                                           |               |
   |        |                            +----------+   |               |
   |        |--(C)---- Access Token ---->|          |   |               |
   |        |                            |          |   |               |
   |        |<-(D)- Protected Resource --| Resource |   | Authorization |
   | Client |                            |  Server  |   |     Server    |
   |        |--(E)---- Access Token ---->|          |   |               |
   |        |                            |          |   |               |
   |        |<-(F)- Invalid Token Error -|          |   |               |
   |        |                            +----------+   |               |
   |        |                                           |               |
   |        |--(G)----------- Refresh Token ----------->|               |
   |        |                                           |               |
   |        |<-(H)----------- Access Token -------------|               |
   +--------+           & Optional Refresh Token        +---------------+
   ```
