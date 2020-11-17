# cf push 동작 방법
- PAS App 개발자가 $ cf push 명령어를 실행 할 때 Pivotal PAS Platform의 주요 동작 방법을 설명합니다 

## 1. cf push에 대한 주요 동작 방법

### 1.1. cf push API 확인
- cf push 명령어가 실행 되게 되면 크게 아래와 같은 API를 호출하게 됩니다.
- API 호출 순서는 Cloud Controller 버전에 따라 상이 할 수 있습니다.

```
1) GET api.sys.{DOMAIN} # API Domain을 호출하여 PAS의 HTTP Endpoint 확인 (uaa, log, doppler, networking 등)
2) GET /login # PAS Login 계정을 확인하여 권한 체크 
3) GET api.sys.{DOMAIN} # API DOmain을 호출하여 cf push에 대한 상세 HTTP Endpoint 확인(buildpack, apps, events, drolets, packages 등)
4) GET /v2/apps?q=name%&q=space_guid% # cf push 대상 APP이 실제 존재 하고 있는 확인
5) GET /v2/shared_domains?q # cf push 대상 APP의 shared domain 확인
6) GET /v2/organizations/private_domains?q=name # 조직 전체에서 App의 도메인을 사용하는지 확인
7) POST /v2/apps # cf push 대상 APP의 정보 생성 (buildpack, instance 수, memory, space 명 기반) 
8) POST /v2/routes # cf push 대상 App의 Route 정보 생성
9) PUT /v2/routes/apps/ # cf push 대상 App과 Route를 연결
10) PUT /v2/resource_match # App의 Build 정보의 전/후의 checksum, size가매칭 되는지 확인
11) PUT /v2/apps/bits?async=true # App이 실행되어야 하는 bit를 정의, bit는 App의 패키이 결과물 또는 Dependency입니다.
12) GET /v2/jobs/ # APP의 Job 수행을 지속적으로 체크 (반복 수행)
13) PUT /v2/apps/ # APP의 상태를 "STARTED"로 변경
14) GET /v2/apps/1 # APP의 상태를 지속적으로 체크 "detected_start_command", "package_state"를 확인
15) GET /v2/apps/instances # APP의 Instance의 Uptime, Since, State를 확인, STAGING -> RUNNING 시 종료
16) GET /v3/apps?names=&space_guids= # APP의 상세 Eedpoint 정보 확인
17) GET /v3/apps/processes/web # App의 Web Process 확인
18) GET /v3/processes/stats # APP의 전체 Process 상태 확인
19) GET /v3/apps/droplets/current  # APP의 최신 Droplet 정보 확인
20) GET /v2/apps/routes # APP의 Route 정보 확인
21) GET /v2/apps/instances # APP의 Instance의 Uptime, Since, State를 확인하고 이상이 없으면 종료
```

### 1.2. cf push 동작 방식 확인
1) Cloud Controller는 Application 배포 명령을 Diego BBS VM에 전달하고 BBS는 Application의 목표 Spec(instance 수, Memory 등)을 저장 합니다.

![ex_screenshot](./images/cf_push_1.png)

2) BBS는 Application의 목표 Spec를 바탕으로 요청을 Diego Brain VM의 Auctioneer 프로세스에 전달 합니다.

![ex_screenshot](./images/cf_push_2.png)

3) Auctioneer는 Diego_Cell VM의 Rep에 Application 배포를 요청하고 Rep는 실제 Container 환경의 Garden Runc의 상태가 정상이면 해당 요청을 승인합니다.

![ex_screenshot](./images/cf_push_3.png)

4) Rep는 Garden Runc를 호출하여 Container를 생성하고 Garden Runc는 $ cf push, Application 배포 시 생성한 Droplet을 Blobstore VM에서 다운로드하고 Garden Container 환경에서 Application을 실행합니다.

![ex_screenshot](./images/cf_push_4.png)

5) Diego Cell VM의 route_emitter는 Application의 접근 경로를 Go Router에 등록 시킵니다.

![ex_screenshot](./images/cf_push_5.png)

6) Diego Cell VM 내부 프로세스 Loggreate agent가 Application의 Log와 Metric 정보를Doppler를 통해 Loggregator Component에 전달하여 사용자에게 4333 PORT로 제공 합니다.

![ex_screenshot](./images/cf_push_6.png)


### 1.3. Diego BBS,Brain,Cell에서 관리되는 실제 App의 Json 형태
- Actual Long-Running Processes (현재 관리 대상의 App Process)
```
{
  "process_guid": "ff947ed3-5306-424c-974c-0650b2e16298-4dd30aa8-08ac-4283-94f6-a4146d4d4566",
  "index": 1,
  "domain": "cf-apps",
  "instance_guid": "c24bb9fa-1200-4fed-5ea3-b5c6",
  "cell_id": "ec062db5-f92e-447c-8a99-876a99245d6b",
  "address": "172.28.101.129",
  "ports": [
    {
      "container_port": 8080,
      "host_port": 61000,
      "container_tls_proxy_port": 61001,
      "host_tls_proxy_port": 61002
    },
    {
      "container_port": 2222,
      "host_port": 61001,
      "container_tls_proxy_port": 61002,
      "host_tls_proxy_port": 61003
    }
  ],
  "instance_address": "10.255.247.4",
  "preferred_address": "HOST",
  "crash_count": 0,
  "state": "RUNNING",
  "since": 1584442717225962800,
  "modification_tag": {
    "epoch": "75fce3d3-9409-4e50-4c1e-19f8d4ea9ee1",
    "index": 11
  },
  "presence": "ORDINARY"
}
```
- Desired Long-Running Processes (목표 대상의 App Process)
```
{
  "process_guid": "29ac9072-fd9e-4d71-9b0c-5039f8b644d8-213b42e9-58dc-41da-b678-e57780cd862b",
  "domain": "cf-apps",
  "rootfs": "preloaded:cflinuxfs3",
  "instances": 2,
  "env": [
    {
      "name": "LANG",
      "value": "en_US.UTF-8"
    }
  ],
  "setup": {
    "serial": {
      "actions": [
        {
          "download": {
            "artifact": "droplet",
            "from": "https://cloud-controller-ng.service.cf.internal:9023/internal/v4/droplets/29ac9072-fd9e-4d71-9b0c-5039f8b644d8/5cb850f5e8a5a75178eb10d34ed132aeba430d5988c1ff3aa1f0835743e0174e/download",
            "to": ".",
            "cache_key": "droplets-29ac9072-fd9e-4d71-9b0c-5039f8b644d8-213b42e9-58dc-41da-b678-e57780cd862b",
            "user": "vcap",
            "checksum_algorithm": "sha256",
            "checksum_value": "5cb850f5e8a5a75178eb10d34ed132aeba430d5988c1ff3aa1f0835743e0174e"
          }
        }
      ]
    }
  },
  "action": {
    "codependent": {
      "actions": [
        {
          "run": {
            "path": "/tmp/lifecycle/launcher",
            "args": [
              "app",
              "JAVA_OPTS=\"",
              ""
            ],
            "env": [
              {
                "name": "PORT",
                "value": "8080"
              },
              {
                "name": "VCAP_APP_PORT",
                "value": "8080"
              },
              {
                "name": "VCAP_APP_HOST",
                "value": "0.0.0.0"
              },
              {
                "name": "TZ",
                "value": "Asia/Seoul"
              },
              {
                "name": "JAVA_OPTS",
                "value": "-DGCS_COMP_CODE=01 -DLANGUAGE_CODE=KOR -DworksCode=K"
              },
              {
                "name": "SPRING_PROFILES_ACTIVE",
                "value": "cloud"
              },
              {
                "name": "VCAP_APPLICATION",
                "value": "{\"cf_api\":\"https://xxx\",\"limits\":{\"fds\":16384,\"mem\":1024,\"disk\":512},\"application_name\":\"xxxxe\",\"application_uris\":[\"xxxx\",\"m2rb05-xxxx\"],\"name\":\"m2rb05-change\",\"space_name\":\"m2r\",\"space_id\":\"6cf09a60-aecc-4dc8-987a-9973df6e7642\",\"organization_id\":\"cbf9f66c-3cf6-4da6-8bf0-faf624807a6b\",\"organization_name\":\"xxx\",\"uris\":[\"xxxx\",\"xxx-xxxx"],\"process_id\":\"29ac9072-fd9e-4d71-9b0c-5039f8b644d8\",\"process_type\":\"web\",\"application_id\":\"29ac9072-fd9e-4d71-9b0c-5039f8b644d8\",\"version\":\"213b42e9-58dc-41da-b678-e57780cd862b\",\"application_version\":\"213b42e9-58dc-41da-b678-e57780cd862b\"}"
              },
              {
                "name": "MEMORY_LIMIT",
                "value": "1024m"
              },
              {
                "name": "VCAP_SERVICES",
                "value": "{}"
              },
              {
                "name": "VCAP_PLATFORM_OPTIONS",
                "value": "{\"credhub-uri\":\"https://credhub.service.cf.internal:8844\"}"
              }
            ],
            "resource_limits": {
              "nofile": 16384
            },
            "user": "vcap",
            "log_source": "APP/PROC/WEB",
            "suppress_log_output": false
          }
        },
        {
          "run": {
            "path": "/tmp/lifecycle/diego-sshd",
            "args": [
              "-address=0.0.0.0:2222",
              "-hostKey=-----BEGIN RSA PRIVATE KEY-----\----END RSA PRIVATE KEY-----\n",
              "-authorizedKey=ssh-rsa",
              "-inheritDaemonEnv",
              "-logLevel=fatal"
            ],
            "env": [
              {
                "name": "PORT",
                "value": "8080"
              },
              {
                "name": "VCAP_APP_PORT",
                "value": "8080"
              },
              {
                "name": "VCAP_APP_HOST",
                "value": "0.0.0.0"
              },
              {
                "name": "TZ",
                "value": "Asia/Seoul"
              },
              {
                "name": "JAVA_OPTS",
                "value": "-DGCS_COMP_CODE=01 -DLANGUAGE_CODE=KOR -DworksCode=K"
              },
              {
                "name": "SPRING_PROFILES_ACTIVE",
                "value": "cloud"
              },
              {
                "name": "VCAP_APPLICATION",
                "value": "{\"cf_api\":\"https://xxxxx",\"limits\":{\"fds\":16384,\"mem\":1024,\"disk\":512},\"application_name\":\"m2rb05-change\",\"application_uris\":[\"xxxx\",\"xxxxx\"],\"name\":\"xxxx",\"space_name\":\"xxx\",\"space_id\":\"6cf09a60-aecc-4dc8-987a-9973df6e7642\",\"organization_id\":\"cbf9f66c-3cf6-4da6-8bf0-faf624807a6b\",\"organization_name\":\"xxxx\",\"uris\":[\"xxxxx\",\"xxxxx\"],\"process_id\":\"29ac9072-fd9e-4d71-9b0c-5039f8b644d8\",\"process_type\":\"web\",\"application_id\":\"29ac9072-fd9e-4d71-9b0c-5039f8b644d8\",\"version\":\"213b42e9-58dc-41da-b678-e57780cd862b\",\"application_version\":\"213b42e9-58dc-41da-b678-e57780cd862b\"}"
              },
              {
                "name": "MEMORY_LIMIT",
                "value": "1024m"
              },
              {
                "name": "VCAP_SERVICES",
                "value": "{}"
              },
              {
                "name": "VCAP_PLATFORM_OPTIONS",
                "value": "{\"credhub-uri\":\"https://credhub.service.cf.internal:8844\"}"
              }
            ],
            "resource_limits": {
              "nofile": 16384
            },
            "user": "vcap",
            "log_source": "CELL/SSHD",
            "suppress_log_output": false
          }
        }
      ]
    }
  },
  "start_timeout_ms": 180000,
  "monitor": {
    "timeout": {
      "action": {
        "parallel": {
          "actions": [
            {
              "run": {
                "path": "/tmp/lifecycle/healthcheck",
                "args": [
                  "-port=8080",
                  "-uri=/health"
                ],
                "resource_limits": {
                  "nofile": 16384
                },
                "user": "vcap",
                "log_source": "HEALTH",
                "suppress_log_output": true
              }
            }
          ]
        }
      },
      "timeout_ms": 600000
    }
  },
```

