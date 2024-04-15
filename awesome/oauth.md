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
