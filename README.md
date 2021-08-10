# atelier-microservices
- In this application, we will proceed by creating a microservices application in which we will explore all aspects of the Microservices Architecture with Spring Boot.
- The application is based on 3 microservices (client-service, , sales-services).

## client-service MS
- Create a client-service application, sous [Spring Initializr](https://start.spring.io/).
- Develop a Client REST Controller ensuring the CRUD operations for a client entity by calling a spring service which in turn uses a dao (repository spring data).
    + Client Entity :
```java
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "client")
public class Client implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    // Attributes ...
    // Getters and Setters ...
    // Methods ...
}
```
+ ClientRepository repository Spring-Data :
```java
import ma.hero.clients.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
  // Methods ...
}
```
+ IClientService interface declaring CRUD operations for the Client entity :
```java
import ma.hero.clients.domain.Client;

import java.util.List;

public interface IClientService {

    Client createClient(Client client);

    Client getClient(Long id);

    List<Client> getAllClients();

    Client updateClient(Client client);

    void deleteClient(Long id);
}
```
+ ClientService service which implements IClientService by calling the ClientRepository repository :
```java
import ma.hero.clients.domain.Client;
import ma.hero.clients.repository.ClientRepository;
import ma.hero.clients.service.IClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientService implements IClientService {

  private final Logger log = LoggerFactory.getLogger(ClientService.class);

  private final ClientRepository clientRepository;

  public ClientService(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  // IClientService implementation (Methods) ...
}
```
+ ClientController Rest controller that uses the ClientService service to expose CRUD operations :
```java
import ma.hero.clients.domain.Client;
import ma.hero.clients.service.IClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
public class ClientController {
    
    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
  
    @Autowired 
    IClientService clientService;

    // Controller Methods ...
}
```
- Customize the configuration (Use profiles to adapt to the execution environment), for example :
```yml
server:
  port: 8000
---
profiles: test
server:
  port: 9000
---
profiles: prod
server:
  port: 9090
```
- Opt for MySQL as DBMS, by adding the mysql-connector to the dependencies in pom.xml file :
```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <scope>runtime</scope>
</dependency>
```
- Create a "clients_db" database, by modifying application.yml file :
```yml
spring:
  jpa:
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
    database: MYSQL
    hibernate:
      ddl-auto: update
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:3306/clients_db?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
    username: root
    password:
```
- Cover the two aspects "Unit Tests" and "Integration Tests":
    + Integration test at the service part using an H2 database (no mocking planned);
    + Unit tests at controller level with Spring Mock MVC and Mockito.
- Add the following dependency to allow testing of the data access layer without using a remote DBMS (with an onboard h2 database) :
```xml
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>test</scope>
</dependency>
```
- To allow SpringBoot to configure the datasource with h2 instead of mysql, you must create a new spring boot "test" profile containing the desired configuration as follows :
```yml
---
spring:
  profiles: test
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: sa
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create
```
- Use the Gson library to convert Java to JSON and vice versa.

### Keycloak
- For a local installation, just unzip the Keycloak version in the working directory.
- To start Keycloak locally, just run the standalone.bat script located in the bin / directory.
- Keycloak is started by default on localhost:8080
- To be able to configure Keycloak, we need an admin account that we can create through the administration console.
#### Keycloak & Spring Boot
Now, we will try to secure the «client-service» application.
- To secure a Spring Boot application with Keycloak, we will need two dependencies, the spring boot keycloak and spring-security starter :
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
<groupId>org.keycloak</groupId>
<artifactId>keycloak-spring-boot-starter</artifactId>
</dependency>
```
- Add a «dependencyManagement» section to manage the keycloak starter :
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.keycloak.bom</groupId>
      <artifactId>keycloak-adapter-bom</artifactId>
      <version>${keycloak.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```
- Mention keycloak.version in the properties of pom.xml :
```xml
<keycloak.version>14.0.0</keycloak.version>
```
- Add a configuration class that will allow Keycloak to be integrated into spring-security and tweak the resulting config :
```java
// imports

@KeycloakConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class KeycloakSecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {
    // ...
}
```
#### Configuration Keycloak
- Creation of a Keycloak domain / realm : ms-workshop.
- Adding a client that corresponds to the resource (Client REST API created) that will request authentication from Keycloak : clients-app.
- Configuration of the client « clients-app » : Access Type -> bearer-only.
- Don't forget to write down the client's secret in the « Credentials » tab.
- Application side configuration :
```yml
keycloak:
  realm: ms-workshop
  auth-server-url: http://localhost:8080/auth
  resource: client-app
  credentials:
    secret: cad938e5-ea3b-4807-a480-ba9e580208cc
  principal-attribute: preferred_username
  bearer-only: true
```
- Start the client-service and go through postman to test the recovery of clients. 
  => 401 Unauthorized !
- To access the API:
  + Create a user in the « ms-workshop » realm: login -> reda / password -> reda ;
  + Create a frontend client with a "public" accessType to simulate a call from the HMI after the user has logged in ;
  + Retrieve a token from Keycloak via curl and inject it into an « Authorization » type header and redo the test with postman :  
    curl -X POST "http://localhost:8080/auth/realms/ms-workshop/protocol/openid-connect/token" -H "Content-Type: application/x-www-form-urlencoded" -d "username=reda" -d "password=reda" -d "grant_type=password" -d "client_id=frontend"  
    Authorization : Bearer ...access_token...
    
#### Securing with a role
- Add the @PreAuthorize annotation which will restrict access to the « getAllClients() » method in ClientController :
```java
@RestController
@RequestMapping("/api/client")
public class ClientController {
    // Controller Methods...
    @GetMapping
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<Client>> getAllClients() {
      log.info("Returning all clients from database.");
      List<Client> clients = clientService.getAllClients();
      return new ResponseEntity<>(clients, HttpStatus.OK);
    }
}
```
The test via postman becomes inconclusive => 403 Forbidden !
- Create a user role in the « ms-workshop » realm ;
- Associate a user with a role through the "Role Mappings" block of the user view ;  
=> A new test via postman returns the expected result.

#### Updating tests
Action: Take charge of the security layer and make the necessary changes.
- Create the ma.hero.clients.config package in the test folder.
- Add the following dependency in pom.xml :
```xml
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-test</artifactId>
  <scope>test</scope>
</dependency>
```
- Add the following class (an annotation) in config package :
```java
package ma.hero.clients.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOAuth2SecurityContextFactory.class)
public @interface WithMockOAuth2Conext {

    String authorities() default "";
}
```
- Create the "WithMockOauth2SecurityContextFactory" class which will initiate a spring-security context by feeding it with the authorities declared in the "WithMockOauth2Context" annotation :
```java
package ma.hero.clients.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockOAuth2SecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2Context> {

    @Override
    public SecurityContext createSecurityContext(WithMockOAuth2Context mockOAuth2Context) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        AccessToken accessToken = new AccessToken();
        accessToken.setName("Test User");

        KeycloakSecurityContext keycloakContext = new KeycloakSecurityContext("someTokenString", accessToken, "1111", null);
        KeycloakPrincipal<KeycloakSecurityContext> principal = new KeycloakPrincipal<KeycloakSecurityContext>("Test User", keycloakContext);
        List<String> securedActions = new ArrayList<>();
        if (!mockOAuth2Context.authorities().isEmpty()) {

            if (mockOAuth2Context.authorities().contains(" ")) {
                StringTokenizer stringTokenizer = new StringTokenizer(mockOAuth2Context.authorities(), " ");
                if (stringTokenizer.countTokens() > 0) {
                    while (stringTokenizer.hasMoreTokens()) {
                        securedActions.add("ROLE_".concat(stringTokenizer.nextToken()));
                    }
                }
            } else {
                securedActions.add("ROLE_".concat(mockOAuth2Context.authorities()));
            }
        }

        RefreshableKeycloakSecurityContext ksc = new RefreshableKeycloakSecurityContext(null, null, "accessTokenString", accessToken, "idTokenString", null, "refreshTokenString");

        SimpleKeycloakAccount account = new SimpleKeycloakAccount(principal, new HashSet<String>(securedActions), ksc);

        context.setAuthentication(new KeycloakAuthenticationToken(account, false));

        return context;
    }
}
```
- Add the annotation « @WithMockOauth2ScopeContext » to the test method « testGetClients » :
```java
// ...
public class ClientControllerTest { 
    // ...
    @Test
    @WithMockOAuth2Context(authorities = "user")
    public void testGetClients() throws Exception {
      Client client1 = new Client("Test1", "Test1", "test1@test.ma", "Address 1");
      Client client2 = new Client("Test2", "Test2", "test2@test.ma", "Address 2");
      when(service.getAllClients()).thenReturn(Stream.of(client1, client2).collect(Collectors.toList()));

      mockMvc.perform(MockMvcRequestBuilders.get("/api/client").accept(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(2))).andReturn();
    }
    // ...
}
```
=> Tests are again conclusive.

#### Swagger Integration with Keycloak
Report: Launch the swagger HMI and retest your API => 401 Unauthorized !
- Create a swagger-ui client with an accessType "confidential" to allow swagger to negotiate a token with Keycloak ;
- Modify the "SwaggerConfig" class as follows :
```java
package ma.hero.clients.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationCodeGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String OAUTH_NAME = "spring_oauth";

    @Value("${keycloak.auth-server-url}")
    private String authServer;

    @Value("${swagger-ui.secret}")
    private String clientSecret;

    @Value("${swagger-ui.client}")
    private String clientID;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${spring.application.name}")
    private String groupName;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("ma.hero.clients.api"))
                .paths(PathSelectors.any())
                .build().securitySchemes(Arrays.asList(securityScheme()))
                .securityContexts(Arrays.asList(securityContext()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Client Management Rest APIs")
                .description("This page lists all the rest apis for Clients Management App.")
                .version("1.0-SNAPSHOT").contact(new Contact("Reda EL GHALLOUCH", "www.hero.com", "ghalou.reda@gmail.com"))
                .build();
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder().realm(realm).clientId(clientID).clientSecret(clientSecret).appName(groupName).scopeSeparator(" ").build();
    }

    private SecurityScheme securityScheme() {
        GrantType grantType = new AuthorizationCodeGrantBuilder().tokenEndpoint(new TokenEndpoint(authServer + "/realms/" + realm + "/protocol/openid-connect/token", groupName)).tokenRequestEndpoint(new TokenRequestEndpoint(authServer + "/realms/" + realm + "/protocol/openid-connect/auth", clientID, clientSecret)).build();
        return new OAuthBuilder().name(OAUTH_NAME).grantTypes(Arrays.asList(grantType)).build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(Arrays.asList(new SecurityReference(OAUTH_NAME, new AuthorizationScope[0]))).forPaths(PathSelectors.any()).build();
    }
}

```
- Add the following config to the application.yml file :
```yml
...
spring:
  application:
    name: client-service
...
swagger-ui:
  client: swagger-ui
  secret: cc3a371e-c495-4b0b-b1eb-c87f50c0f103
```
- Restart the client-service application, launch the swagger HMI, click on Authorize and Connect.

#### Use Postman to request APIs via Keycloak (by script)
```javascript
const tokenUrl = 'http://localhost:8080/auth/admin/master/console/realms/ms-workshop/protocol/openid-connect/token';

const retrieveTokenRequest = {
  method: 'POST',
  url: tokenUrl,
  body: {
    mode: 'urlencoded',
    urlencoded: [
      { key: 'grant_type', value: 'password' },
      { key: 'client_id', value: 'swagger-ui' },
      { key: 'client_secret', value: 'cc3a371e-c495-4b0b-b1eb-c87f50c0f103' },
      { key: 'username', value: 'reda' },
      { key: 'password', value: 'reda' }
    ]
  }
};

pm.sendRequest(retrieveTokenRequest, (err, response) => {
  console.log(response);
  const jsonResponse = response.json();
  const token = jsonResponse.access_token;

  pm.variables.set('access_token', token);
});
```

### Implementing advanced REST clients with Spring Cloud and Netflix Feign
#### Declarative REST Client: Feign
- To begin with, we are going to create a new service: « sales-service » ;
- To retrieve the sales of a client X, « client-service » uses a Feign client (SalesClient) to call « sales-service » which exposes the path « / api / sales / client / {clientId} » ;
- Develop the « sales-service » CRUD ;
- In the pom of the two projects « client-service » and « sales-service », we will add :
```xml
<properties>
  ...
  <spring-cloud.version>Hoxton.SR8</spring-cloud.version>
</properties>
...
...
<dependencyManagement>
  ...
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-dependencies</artifactId>
      <version>${spring-cloud.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```
- Add the following dependency (only for « client-service ») :
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```
- Add the @EnableFeignClients annotation in « ClientServiceApplication » :
```java
@SpringBootApplication
@EnableSwagger2
@EnableFeignClients
public class ClientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientServiceApplication.class, args);
	}

}
```
- In « sales-service », add getSalesByClient() method in "SaleController" and add its service method ;
- In « client-service », create a package ma.hero.clients.api.feign and another ma.hero.clients.dto ;
- Add the SaleDto class which contains the attributes of the Sale class with the getters and setters;
- Add the feign SalesClient interface :
```java
package ma.hero.clients.api.feign;

import ma.hero.clients.dto.SaleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "sales-service", url = "http://localhost:9001")
public interface SaleClient {

    @GetMapping("/api/sale/client/{id}")
    List<SaleDto> getClientSales(@PathVariable("id") Long clientId);
}

```
- In "ClientController", add the injection of the "SaleClient" client :
```java
  @Autowired
  private SaleClient saleClient;
```
- Then, add the following method to expose the path « / api / client / {id} / sales » :
```java
  // ------------------- Retrieve Client Sales -----------------------------------------
  @GetMapping(value = "/{id}/sales")
  public ResponseEntity<List<SaleDto>> getClientSales(@PathVariable("id") long id) {
      List<SaleDto> sales = saleClient.getClientSales(id);
      return new ResponseEntity<>(sales, HttpStatus.OK);
  }
```
- Start the both applications and test the new path REST via Swagger.  
  => Tests are again conclusive.
