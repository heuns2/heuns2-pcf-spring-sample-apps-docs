# cf App - config-server 동작 방법
- cf env 화면에서 언급되었듯이 Spring Boot는 application.properties를 통해 소스 코드와 설정 부분을 분리 할 수 있습니다. 그로인하여 다양한 배포 환경(dev, prod, test, local) 등과 같이 배포 환경을 여러개로 나눌수 있습니다. 하지만 이 때 발생한 문제점이 배포 환경을 변경 시에 Source Code의 새로운 Build와 Restart가 필요합니다.
- 또한 Scale Out을 통하여 많은 량의 Traffic을 처리하는 App에 대해서 속성 값의 일괄 적용이 필요해졌습니다. 이를 위해 External Reposistory(git) - Config 설정 처리(Config-Server)를 두고 중앙에서 관리하는 시스템을 구성하는 방법입니다.
- p-Spring Config Server Tile을 설치하게 되면 아래와 같은 형상으로 VM이 배포 되게 됩니다. 
```
bosh -d p_spring-cloud-services-d6774566abb4f589ab14 vms
Using environment 'xxx.xxx.xxx.xxx' as client 'ops_manager'

Task 150299. Done

Deployment 'p_spring-cloud-services-xxxxxx'

Instance                                                    Process State  AZ   IPs             VM CID                                   VM Type     Active  Stemcell 
spring-cloud-services/xxxxx-xxxx-xxxx-xxxx-xxxx  running        az1  xxx.xxx.xxx.xxxx  vm-xxxx-xxxx-xxxxx-xxxxx-xxxxxx  medium.mem  true    bosh-vsphere-esxi-ubuntu-xenial-go_agent/315.154
```

- VM 내부에서는 Java로 동작되는 Mirror Service가 존재하며 Persistence Disk 영역에 Config Server 대상의 Git Repo가 Clone(Clone Mirror) 됩니다.
- Mirror Service에서 Clone 된 Code들은 실제 Bind 한 App들에서 SSH 프로토콜을 통해 연동이 되어 있고 Spring Boot 실행 시 Property들을 환경 변수로 생성 합니다. 

- p-Spring-Config-Server 아키텍처

 ![config-server-ac](./images/config-server-ac.PNG)

## 1. 기본적인 Spring-Config Server 사용 방법
- Pivotal Cloud Server 중 Config-Server App에 대한 정리입니다.

#### 1.1. 준비 사항
1)  github application properties 파일 지정, TEST 용으로 prod, staging, dev를 생성하였습니다.
- application properties 파일의 Rule은 application-{prefix}.properties입니다.
- 예시

```
---
spring:
  profiles: dev # dev, prod, staging 으로 변경
  application:
    name: test-boot-app

config:
  profile: dev # dev, prod, staging 으로 변경
  message: hello dev # dev, prod, staging 으로 변경
```
2) source code
- application.yml 또는 bootstrap.yml

```
---
spring:
  #profiles: cloud
  application:
    name: test-boot-app
  cloud:
    config:
      server:
        git:
          uri: http://github.xxxxx.com/scm/xxxx/xxxx-xxxx.git
          username: xxxxx
          password: xxxxx
          searchPaths: /study/cf/cf-config-server
```
- Spring boot Start 또는 cf app push 후 연동 route를 통해 config property 정보 추출 할 수 있습니다.

- dev 결과
 ![ex_screenshot](./images/config-dev.PNG)

- staging 결과
 ![ex_screenshot](./images/config-staging.PNG)

- prod 결과
 ![ex_screenshot](./images/config-prod.PNG)

- 해당 /config/{fix_name} endpoint를 통해 HTTP/HTTPS REST API를 통하여 환경 변수를 가져 올 수 있습니다, config-server 사용 client에서 server 쪽으로 해당 Endpoint를 통해 env를 가져옵니다.
- 해당  spring.cloud.config.server 설정과 Pivotal Config Server의 가장 큰 차이점은 UAA 인증 절차, 인증 Key Pair 사용 입니다. Pivotal Config Server Endpoint에 접근하기 위해서는 PAS 내부 Uaa Component 인증 절차를 거치게 됩니다.
- 실제 Pivotal Config Server에서 정보를 가져오는 동작 방법 (해당 명령을 통하여 현재 Running 상태인 App이 사용하는 Properties를 확인 할 수 있습니다.)

```
# {APP_NAME}으로 가져오는 이유는 git repo 구성과 service create 생성에 따라 달라집니다. 
$ curl \
 -H "Authorization: Bearer $TOKEN" \
 "https://config-server-xxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxx.{DOMAIN}/{APP_NAME}/{PREFIX}"

# 결과 값 예시
{"name":"eai-adapter","profiles":["cloud"],"label":null,"version":null,"state":null,"propertySources":
[{"name":"credhub-eai-adapter-cloud-master","source":{}},
{"name":"ssh://mirror@xxx.xxx.xxx.xxxx:22/var/vcap/store/mirror/d810d1644566311d5b1da37872c0312d/study/{APP_NAME}/application-cloud.yml",
"source":{"spring.http.converters.preferred-json-mapper":"gson","eureka.client.register-with-eureka":true,"datasource.driver.class.name":"${vcap.services.db-service-ma3a01.credentials.oracle-driver}"
...생략
```

- 실제 Pivotal Config Server에 Mapping 된 외부 저장소 정보

```
git:
  cloneOnStart: false
  periodic: true
  privateKey: |
    -----BEGIN RSA PRIVATE KEY-----
    -----END RSA PRIVATE KEY-----
  refreshRate: 60
  searchPaths: '{application}'
  sourcePassword: xxxxxxx
  sourceUri: http://git.xxxxxx.xx.xxxx/scm/xxxx/config-repo
  sourceUsername: xxxxxx
  uri: ssh://mirror@xxx.xxx.xxx.xxx:22/var/vcap/store/mirror/d810d1644566311d5b1da37872c0312d/study
```

- 실제 Pivotal Config Server에 Mapping 된 Spring Security 정보 (PAS 내부의 UAA를 사용함으로 추후 client app -> p-config-server의 모든 endpoint 접근 시 UAA에 대한 설정이 필요합니다.)

```
  security:
    oauth2:
      client:
        registration:
          uaa:
            provider: uaa
            client-id: ${scs.dashboard.client-id}
            client-secret: ${scs.dashboard.client-secret}
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            authentication-method: basic
            scope: openid,cloud_controller.read
        provider:
          uaa:
            token-uri: ${uaa.token-uri}
            authorization-uri: ${uaa.authorization-uri}/oauth/authorize
            jwk-set-uri: ${uaa.jwk-set-uri}
            user-info-uri: ${uaa.uri}/userinfo
            user-name-attribute: user_name
      resourceserver:
        jwt:
          issuer-uri: ${uaa.token-uri}
          jwk-set-uri: ${uaa.jwk-set-uri}
```

## 2. 기본적인 p-Spring-Config Client 사용 방법
- Pivotal Cloud Server 중 Config-Server App에 Bind 된 App에 대한 정리 입니다.

### 2.1 준비사항
- PAS Config Server를 사용하기 위해 Service Instance 생성합니다.

```
cf create-service  p.config-server standard config-service-test -c '{"git": {"password": "xxxxxxxx!@", "searchPaths": "/study/cf/cf-config-server", "cloneOnStart": false, "uri": "http://github.xxxxxx.xxxxxx.kr/scm/xxxx/study", "username": "xxxxx" , "refreshRate": 60, "periodic": true}}'
```

### 2.2. Local에서 Pivotal Config Server 사용
- PAS의 App 또는 Bind-Service를 하지 않은 Local에서 Pivotal Config Server를 사용하기 위해서는 몇가지 정보가 필요 합니다. Pivotal Config Server는 기본적으로 Endpoint에 접근하기 위해 Spring Security Access Token이 사용 됩니다.
- 만약 자체 서명 인증서를 사용 할 경우 신뢰 하는 인증서에 저장해야 합니다. 

- 아래 정보를 Sample App의 bootstrap.yml에 설정합니다.

```
"client_secret": "xxxxxxx",
"uri": "https://config-server-a8104a93-b782-40be-a07c-2003b873dc09.apps.xxxxx.xxxxx.co.kr",
"client_id": "config-client-xxxxxx",
"access_token_uri": "https://uaa.sys.xxxxx.xxxx.co.kr:443/oauth/token"
```

- bootstrap.yml은 application.yml 보다 먼져 설정이 되며 boot가 run 되기 직전에 실행 되는 파일입니다.
- bootstrap.yml sample

```
---
spring:
  application:
    name: test-boot-app
  profiles:
    active: dev
  cloud:
    config:
      uri: https://config-server-a8104a93-b782-40be-a07c-2003b873dc09.apps.xxx.xxx.co.kr
      fail-fast: false
      client:
        oauth2:
          client-id: config-client-xxxx-221e-49b6-xxxx-0e2f680bf0ca
          client-secret: xxxxx
          access-token-uri: https://uaa.sys.xxxx.xxxx.co.kr:443/oauth/token
```

### 2.3. PAS에서 Pivotal Config Server 사용
- PAS에서 Config-Server를 사용 할 경우 bootstrap.yml에 별도의 Config Server Client 설정 없이 Bind-Service를 통해 자동화 됩니다.
- bootstrap.yml Sample (2.2에서 config-client 속성들을 전체 제거)

```
---
spring:
  application:
    name: test-boot-app
```

- manifest.yml (SPRING_PROFILES_ACTIVE 환경 변수를 통해 적용 할 application.yml의 PREFIX를 지정)
```
---
applications:
- name: my-app
  memory: 1G
  buildpack: java_buildpack_offline
  path: ./target/test-boot-0.0.1-SNAPSHOT.jar
  services:
    - config-service-test
  env:
    SPRING_PROFILES_ACTIVE: staging
```

- PAS에 App 배포

```
$ cf push -f manifest.yml
```

- cf push APP Log에서 적용 되는 Config를 확인 합니다.

```
main] b.c.PropertySourceBootstrapConfiguration : Located property source: OriginTrackedCompositePropertySource
{name='configService', propertySources=[OriginTrackedMapPropertySource {name='credhub-test-boot-app-staging-master'},
OriginTrackedMapPropertySource {name='ssh://mirror@xxx.xxx.xxx.xxx:22/var/vcap/store/mirror/489db70bcd2e80ca5a569bb6af25af03/platform-mgmt/study/cf/cf-config-server/application-staging.yml'}]}
```

- 결과

 ![ex_screenshot](./images/p-config-client.PNG)



## 3. P-Spring Config Server TIP

### 3.1. Config Server의 SearchPaths
- create-service 또는 update-service를 통해 config-server를 설정 할 수 있습니다. 이때 git.searchPaths를 지정 할 수 있는데, git clone path에서의 특정 PATH를 지정 하는 설정입니다. 해당 환경은 아래와 같이 "searchPaths": "{application}"를 설정하여 config-repo/{APP_NAME}으로 설정 됩니다.

### 3.2. Config Server의 Mapping 정보
- 현재 Config Client에서 적용 되는application.yml이나 env를 확인하고 싶을 경우 별도의 Actuator Endpoint를 통해 접근이 가능 합니다.  

#### 3.2.1. Health

```
$ curl  -H "Authorization: Bearer $TOKEN"  "https://my-app.apps.xxxx.xxxx.co.kr/cloudfoundryapplication/health" -k | jq .

# 결과 값 일부 발췌
"clientConfigServer": {
  "status": "UP",
  "details": {
    "propertySources": [
      "credhub-test-boot-app-staging-master",
      "ssh://mirror@xxx.xxx.xxx.xxx:22/var/vcap/store/mirror/489db70bcd2e80ca5a569bb6af25af03/platform-mgmt/study/cf/cf-config-server/application-staging.yml"
    ]
  }
}
```

#### 3.2.2. env

```
$ curl  -H "Authorization: Bearer $TOKEN"  "https://my-app.apps.xxxx.xxxx.co.kr/cloudfoundryapplication/env" -k | jq .

# 결과 값 일부 발췌
{
  "name": "configService:credhub-test-boot-app-staging-master",
  "properties": {}
},
{
  "name": "configService:ssh://mirror@xxx.xxx.xxx.xxx:22/var/vcap/store/mirror/489db70bcd2e80ca5a569bb6af25af03/platform-mgmt/study/cf/cf-config-server/application-staging.yml",
  "properties": {
    "spring.profiles": {
      "value": "staging"
    },
    "spring.application.name": {
      "value": "test-boot-app"
    },
    "config.profile": {
      "value": "staging"
    },
    "config.msg": {
      "value": "hello staging"
    }
  }
}
```

### 3.3. Config Server의 Refresh 정보
- 현재 Config-Client의 정보는 Restart, Restage (Spring Boot 재기동) 시 신규 정보가 등록 됩니다. 하지만 아래와 같은 @RefreshScope를 사용하고 Actuator Endpoint 정보를 통해 Spring Boot 재기동 없이 신규 정보를 반영 할 수 있습니다.

```
@Service
@RefreshScope
public class ConfigServerService
```

```
$ curl  -H "Authorization: Bearer $TOKEN"  "https://my-app.apps.{DOMAIN}/cloudfoundryapplication/refresh" -X POST  -k | jq .
HTTP/1.1 200 OK
```


### 3.4. Config Server의 Git Repo 정보 변경
- Config Server의 정보 변경 시 버그 또는 의도된 기능인지는 모르겠으나 update-servce config-service -c {JSON}으로 변경 할 경우 에러가 발생하게 됩니다.
- 원인은 Config-Server App의 정보는 변경 되지만 외부 GIT Repo 정보를 직접적으로 Mirroring 하는 SCS-Broker VM의 정보는 변경 되지 않기 때문입니다.
- SCS-Broker VM에 접속하셔서 /var/vcap/store/mirror/d810d1644566311d5b1da37872c0312d/config-repo/config 파일에서 git 정보를 수동 수정해야합니다.

### 3.5. Config Server의 LOG
- 만약 Config Client (일반 APP)이 Config Server에 Properties들의 조회를 요청 할 경우 Config-Server APP에는 Credhub 조회와 함께 아래아 같은 로그들이 기록 됩니다. 

- application.yml 파일이 존재하지 않거나 권한이 잘못 되었을 경우, Yaml 형식이 잘 못되었을 경우 Config-Server에서 Exception Log가 출력되어 에러를 찾을 가능성이 있습니다.

```
2020-04-16T13:41:28.81+0900 [APP/PROC/WEB/0] OUT [http-nio-8080-exec-5] INFO  o.s.c.c.s.e.NativeEnvironmentRepository.clean - Adding property source: file:/home/vcap/app/config-repo-default/study/cf/cf-config-server/application-staging.yml
2020-04-16T13:41:28.82+0900 [APP/PROC/WEB/0] OUT [http-nio-8080-exec-5] DEBUG o.s.web.servlet.DispatcherServlet.logResult - Completed 200 OK
2020-04-16T13:41:28.82+0900 [APP/PROC/WEB/0] OUT [http-nio-8080-exec-5] DEBUG o.s.s.w.a.ExceptionTranslationFilter.doFilter - Chain processed normally
2020-04-16T13:41:28.82+0900 [RTR/2] OUT config-server-a8104a93-b782-40be-a07c-2003b873dc09.apps.{DOMAIN} - [2020-04-16T04:41:28.372594653Z] "GET /test-boot-app/staging HTTP/1.1" 200 0 466 "-" "Java/1.8.0_232" "172.28.106.7:52860" "172.28.106.207:61002" x_forwarded_for:"172.28.106.7" x_forwarded_proto:"https" vcap_request_id:"08ec0d40-da3a-4cc2-74f0-7e437a2bd1e0" response_time:0.449207 gorouter_time:0.000245 app_id:"407953b4-7cc5-4720-b652-b1aee04cd10a" app_index:"0" x_b3_traceid:"e9bb2ad5482caa8b" x_b3_spanid:"e9bb2ad5482caa8b" x_b3_parentspanid:"-" b3:"e9bb2ad5482caa8b-e9bb2ad5482caa8b"
```



