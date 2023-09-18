### wso2is

1. indroduce

   - processor
     ![avatar](/static/image/oauth/is-processor.png)
   - 调用不提供 Authorization: Basic BASE64(username:password) 会出现 401, 因为使用 ISServer 需要登录;
   - 随意修改 signature, 在提供 Authorization: Basic BASE64(username:password) 情况下, 都可以验证 token 通过
   - 调用时提供 token 的 header 或 payload 错误会出现 401;
   - 调用时提供 token 的 signature 的值不能随意修改;

2. config OAuth function

   - Inbound Authentication Configuration --> OAuth/OpenID Connect Configuration --> Config --> enable Enable Audience Restriction and choose Allowed Grant Types

3. get access token

   ```shell
   curl -v -k -X POST --user OAUTH_CLIENT_KEY:OAUTH_CLIENT_SECRET -H "Content-Type: application/x-www-form-urlencoded;charset=UTF-8" -d "grant_type=client_credentials&username=admin&password=admin" https://101.132.45.28:9443/oauth2/token
   ```

4. code to get token

   ```c#
   // mapping data model
   public class JWTTokenModel
   {
       [JsonProperty("access_token")]
       public string AccessToken { get; set; }

       [JsonProperty("refresh_token")]
       public string RefreshToken { get; set; }

       [JsonProperty("token_type")]
       public string TokenType { get; set; }

       [JsonProperty("expires_in")]
       public int ExpiressIn { get; set; }
   }


   // app settings
   "JWTConfiguration": {
       "BaseUrl": "https://101.132.45.28:9444",
       "Timeout": "10000",
       "GrantType": "client_credentials",
       "UserName": "admin",
       "Password": "admin",
       "OAuthClientKey": "OAUTH_CLIENT_KEY",
       "OAuthClientSecret": "OAUTH_CLIENT_SECRET"
     }


   // startup register config model: JWTConfiguration
   public IConfiguration Configuration { get; }
   public void ConfigureServices(IServiceCollection services)
   {
     services.Configure<JWTConfiguration>(Configuration.GetSection("JWTConfiguration"));
   }


   // get token
   public class JWTTokenClient : IJWTTokenClient
   {
       private const string Path = "/oauth2/token";
       private readonly string _baseUrl;
       private readonly int _timeout;
       private readonly string _grantType;
       private readonly string _userName;
       private readonly string _password;
       private readonly string _oAuthClientKey;
       private readonly string _oAuthClientSecret;
       private readonly ILogger<JWTTokenClient> _logger;

       public JWTTokenClient(ILogger<JWTTokenClient> logger,
           IOptions<JWTConfiguration> config)
       {
           _logger = logger;
           _baseUrl = config.Value.BaseUrl;
           _timeout = config.Value.Timeout;
           _grantType = config.Value.GrantType;
           _userName = config.Value.UserName;
           _password = config.Value.Password;
           _oAuthClientKey = config.Value.OAuthClientKey;
           _oAuthClientSecret = config.Value.OAuthClientSecret;
       }
       public JWTTokenModel getAccessToken()
       {
           var request = new RestRequest(Path, Method.POST) { Timeout = 10000 };
           request.AddHeader(RequestHeaders.CONTENT_TYPE, RequestHeaders.FORM_CONTENT);
           StringBuilder sb = new StringBuilder();
           sb.Append("grant_type=").Append(_grantType)
               .Append("&username=").Append(_userName)
               .Append("&password=").Append(_password);
           var client = new RestClient(_baseUrl);
           client.Authenticator = new HttpBasicAuthenticator(_oAuthClientKey, _oAuthClientSecret);
           request.AddParameter(RequestHeaders.FORM_CONTENT, sb.ToString(), ParameterType.RequestBody);
           client.RemoteCertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) => true;
           var response = client.Execute(request);

           if (response.IsSuccessful)
           {
               return JsonConvert.DeserializeObject<JWTTokenModel>(response.Content);
           }
           else
           {
               _logger.LogWarning("Get token fialed from wso2 is, cause by: " + response.ErrorException.StackTrace);
           }

           return null;
       }
   }


   // validate token
   // controller
   [Route("/[controller]/[action]")]
   [Authorize]
   [ApiController]
   public class RatingServiceController : ControllerBase{}

   // startup.cs register
   public class Startup
   {
       private const string jwksPath = "/oauth2/jwks";
       public JwtBearerConfig JwtBearerConfig { get; set; }
       public IConfiguration Configuration { get; }

       public Startup(Configuration configuration)
       {
           Configuration = configuration;

           JwtBearerConfig = new JwtBearerConfig();
           Configuration.GetSection("JwtBearerConfig").Bind(JwtBearerConfig);

           Configuration = builder.Build();
       }

       public void ConfigureServices(IServiceCollection services)
       {
           ...
           ConfigOAuth(services);
           ...
       }

       public void Configure(IApplicationBuilder app, IHostingEnvironment env)
       {
           // Notice: this should be placed before UseMvc
           app.UseAuthentication();
           app.UseMvc();
       }

       private void ConfigOAuth(IServiceCollection services)
       {
           var client = new RestClient(JwtBearerConfig.BaseUrl);
           client.RemoteCertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) => true;
           var request = new RestRequest(jwksPath, Method.GET);
           var jwtKey = client.Execute(request).Content;
           var Ids4keys = JsonConvert.DeserializeObject<Ids4Keys>(jwtKey);
           var jwk = Ids4keys.keys;

           services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
               .AddJwtBearer(options =>
               {
                   options.TokenValidationParameters = new TokenValidationParameters
                   {
                       ValidateIssuer = JwtBearerConfig.ValidateIssuer,
                       ValidateIssuerSigningKey = JwtBearerConfig.ValidateIssuerSigningKey,
                       IssuerSigningKeys = jwk,

                       ValidateAudience = JwtBearerConfig.ValidateAudience,
                       ValidAudience = JwtBearerConfig.ValidAudience,

                       ValidateLifetime = JwtBearerConfig.ValidateLifetime,
                       RequireExpirationTime = JwtBearerConfig.RequireExpirationTime
                   };
               }
           );
       }
   }

   public class Ids4Keys
   {
       public JsonWebKey[] keys { get; set; }
   }
   ```
