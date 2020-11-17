# Pivotal Service Registry Server 구성 정보

- Pivotal SCS Serivce 중 Service Registry(Eureka Server)의 구성 정보를 설명합니다.
- 이 문서는 v3.1.5 기반으로 구성되었습니다.

## 1. 설정 정보 확인

### 1.1. cf cli create/update-service를 통해 변경 가능한 값

-  cf cli update-service나 create/update Json을 통해 변경 가능한 값은 아래와 같습니다. 아래의 설정 이외에 모든 설정은 default 또는 제품 내부에서 설정되어 변경 불가입니다.


```
  "properties": {
    "count": {
      "description": "The number of instances for the service registry backing application",
      "type": "integer",
      "minimum": 1
    },
    "peer-timeout": {
      "description": "The peer node timeout",
      "type": "integer"
    },
    "upgrade": {
      "description": "Flag to upgrade the SI",
      "type": "boolean"
    },
    "peers": {
      "type": "array",
      "items": {
        "$ref": "#/definitions/peersConfig"
      }
```

### 1.2. Pivotal Service Registry(Eureka Server) 주요 Default 설정 값
- Pivotal Service Registry에 대한 Mapping 되어 있는 주요 Default 설정 값은 아래와 같습니다.
- renewal의 count는 Client에서 보내는 Heart Beat의 수입니다.
- peer는 다른 Eureka Server의 Registry 정보를 복제하여 복원력과 가용성을 높입니다.
- eureka.instance.instanceId를 입력하지 않으면 자동으로 spring.application.name로 service 명을 생성합니다.


#### 1.2.1. Eureka Server

```
renewalThresholdUpdateIntervalMs: 60000 # renewalThreshold가 update 되는 주기 60s
registrySyncRetryWaitMs: 30000 # Eureka Server가 시작 시 새로 Data, Instance를 받아오는데 대기하는 시간
expectedClientRenewalIntervalSeconds: 30 # Client App에서 Renewal를 보내는 예상 시간 
renewalPercentThreshold: 0.85 # renewal Threshold의 %, 네트워크나 기타 장애 발생 으로 인한 85% 이하면  self preservation mode가 켜지고 Client를 보존
disableDelta: false # Registry를 가져 오기는 30 초마다 예약 된 비동기 작업
numberOfReplicationRetries: 5 # peer 동기화 시 다른 node에서 data를 가져오는 횟 수
enableSelfPreservation: false # 자가 보존 모드 off 상태..Pivotal에서는 peer 사용으로 인하여 SelfPreservation는 사용하지 않는 것 같습니다.
waitTimeInMsWhenSyncEmpty: 90000 # Eureka Server에서 Error가 발생 할 경우 에러를 
evictionIntervalTimerInMs: 60000 # 유효 하지 않은 Client를 삭제하는 Interval 시간

```


#### 1.2.2. Eureka Client

```
fetchRegistry: true # Eureka Registry를 가져와 local로 캐시합니다.
registryFetchIntervalSeconds: 30 # Eureka Registry를 새로고침하는데 걸리는 시간
eurekaConnectionIdleTimeoutSeconds: 30 # Eureka 서버에 연결 한 후 idle 대기 시간
initialInstanceInfoReplicationIntervalSeconds: 30 # Instnace 정보가 처음 Eureka에 등록 되는데 걸리는 시간
eurekaServiceUrlPollIntervalSeconds: 300 # Eureka Server의 URL 변경 시 새로 Polling 되는데 걸리는 시간
```


#### 1.2.3. Eureka Instance

```
leaseRenewalIntervalInSeconds: 30 # Heart Beat 전송 Interval 시간
leaseExpirationDurationInSeconds: 90 # Heart Beat가 전송 되지 않으면 Instance를 제거 대기하는 시간
statusPageUrlPath: /actuator/info # Instnace의 Status 확인 URL
healthCheckUrl: http://{DOMAIN}:8080/actuator/health # Instance의 Health Check Endpoint
```
