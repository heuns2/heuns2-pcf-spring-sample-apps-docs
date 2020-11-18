#  Hikari 연동 Sample App

-  Spring에서 DB에서 사용되는 Connection Pool은 연결(Connection)들을 미리 만들어 두고 DB에 접근시 Pool에 남아있는 Connection하나를 받아와서 사용한 뒤 반환하는 것을 말합니다.
- Spring Datastore HikariCP는 이러한 Pool들을 설정할 수 있으며 주요 설정에 대한 속성은 아래와 같습니다. 

```
connectionTimeout: Client가 Pool에서 Connection을 기다리는 최대 시간 (ms)을 제어합니다. 해당 시간을 초과하면 SQLException이 발생합니다. default 30000ms
idleTimeout: Connenction이 Pool에서 idle 상태로 유지 될 수 있는 시간입니다. 해당 설정은 minimumIdle > maximumPoolSize의 경우에 적용 됩니다. default 600000ms
maxLifetime: 이 속성은 Pool에서 Connection의 최대 수명을 제어합니다. 사용중인 Connection은 폐기되지 않으며 닫혀있을 때만 제거됩니다.
minimumIdle: 이 속성은 HikariCP가 Pool에서 유지하려고 시도 하는 최소 Idle Connection 수를 제어합니다.
maximumPoolSize: 이 속성은 Idle 및 사용중인 Connection을 모두 포함하여 Pool이 도달 할 수있는 최대 크기를 제어합니다.
poolName: JMX에서 식별 할 수 있는 Pool의 명칭
```

## 1. Hikari Java Source Code 주요 설정

- pom.xml

```
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>2.7.8</version>
</dependency>
<dependency>   
    <groupId>mysql</groupId>   
	<artifactId>mysql-connector-java</artifactId>
	<version>5.1.45</version>
</dependency>
```

- application.yml

```
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    url: ${url}
    username: ${user_name}
    password: ${user_password}
    hikari:
      maximum-pool-size: 15
      minimum-idle: 1
      driver-class-name: com.mysql.jdbc.Driver
    jdbc-url: ${jdbc-url}

management:
  endpoints:
    jmx:
      exposure:
        include: health, info
```

- Hikari CP 연동 부분

```
@Value("${spring.application.name}")
private String applicationName;
@Value("${spring.datasource.url}")
private String url;
@Value("${spring.datasource.username}")
private String username;
@Value("${spring.datasource.password}")
private String password;
@Value("${spring.datasource.driver-class-name:oracle.jdbc.OracleDriver}") 
private String driverClassName;
@Value("${spring.datasource.hikari.maximum-pool-size}") 
private String maxPoolSize;
@Value("${spring.datasource.hikari.minimum-idle}") 
private String minimumIdle;
    
@Bean(name = "dataSource")
public DataSource primaryDataSource() {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(driverClassName);
    config.setJdbcUrl(url);
    config.setUsername(username);
    config.setPassword(password);
    config.setMaximumPoolSize(Integer.parseInt("10"));
    config.setMinimumIdle(Integer.parseInt(minimumIdle));
    config.setMaxLifetime(5000);
    config.setIdleTimeout(3000);
    config.addDataSourceProperty("v$session.program", applicationName);
    config.addDataSourceProperty("useUnicode", "true");
    config.addDataSourceProperty("characterEncoding", "utf8");
    config.setConnectionTestQuery("SELECT 1 FROM DUAL");
    DataSource ds = new HikariDataSource(config);
    return ds;
}
```

