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
import ma.s2m.clients.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
  // Methods ...
}
```
+ IClientService interface declaring CRUD operations for the Client entity :
```java
import ma.s2m.clients.domain.Client;

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
import ma.s2m.clients.domain.Client;
import ma.s2m.clients.repository.ClientRepository;
import ma.s2m.clients.service.IClientService;
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
import ma.s2m.clients.domain.Client;
import ma.s2m.clients.service.IClientService;
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
