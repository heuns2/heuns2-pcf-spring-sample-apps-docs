#  Pivotal Cloud Cache 연동 Sample App

- Pivotal Cloud Cache(Gemfire, Geode)  Server와 연동하는 Java Client App에 대해 주요 속성을 설명합니다.
- Gemfire Java Client를 생성 시 Region 구조를 아래와 같은 형태로 생성 할 수 있습니다, 해당 Option을 사용함으로써 Local - PCC Server간의 Proxy를 결정이 되는 것 같으며 옵션에서도 Eviction, Overflow와 같은 속성을 지정 할 수 있습니다.
- 개발자들의 Local에 Cache를 저장하고 있다는 말도 해당 속성 중 1개를 사용 한 것으로 추측됩니다. ex) CACHING_PROXY
- 특이 사항으로는 Client를 통해서는 실제 Gemfire Server에 대한 몇가지 요청은 사용되지 못하며, 해당 기능을 사용 해야 할 경우 gemfire function 기능을 통해 내부에서 gfsh 명령어를 통해 사용 해야 합니다. 예시로는 Cache Server에 특정 Partition Region의 Replica Data를 확인하는 Interface를 구현하게 되어 실행 시킬 경우 Not Found가 발생합니다.


## 1. Gemfire Java Source Code 예시

- Gemfire 연동 부분
```
@Value("${pcc.service-id:cache-service}")
public String PCC_SERVICE_ID;

@Bean(name = "testRegion")
ClientRegionFactoryBean m2bRegion(@Autowired ClientCache gemfireCache) {
    return getClientRegionFactoryBean(gemfireCache, "testRegion");
}

private ClientRegionFactoryBean getClientRegionFactoryBean(ClientCache gemfireCache, String name) {
    ClientRegionFactoryBean f1Region = new ClientRegionFactoryBean<>();
    f1Region.setName(name);
    f1Region.setCache(gemfireCache);
    f1Region.setClose(false);
    f1Region.setShortcut(ClientRegionShortcut.PROXY);
    f1Region.setLookupEnabled(true);
    return f1Region;
}
```

- Gemfire Region에 Data 관리

```
@GetMapping("/caches/{regionName}/{cacheKey}")
public String getCache(@PathVariable("regionName") String cacheName, @PathVariable("cacheKey") String cacheKey) {
    List<String> list = new ArrayList<String>();
    cacheKey = cacheKey.replace("|", "/");
    Region region = this.getRegion(cacheName);
    return region.get(cacheKey).toString();
}


@PutMapping("/caches/{regionName}/{cacheKey}/{cacheValue}")
public String putCache(@PathVariable("regionName") String cacheName, @PathVariable("cacheKey") String cacheKey, @PathVariable("cacheValue") String cacheValue) {
    cacheKey = cacheKey.replace("|", "/");
    Region region = this.getRegion(cacheName);
    region.put(cacheKey.toString(), cacheValue.toString());
    String result = cacheKey.toString() + cacheValue.toString();
    return result;
}

@DeleteMapping("/caches/{regionName}/{cacheKey}")
public String deleteCache(@PathVariable("regionName") String cacheName, @PathVariable("cacheKey") String cacheKey) {
    cacheKey = cacheKey.replace("|", "/");
    Region region = this.getRegion(cacheName);
    region.remove(cacheKey.toString());
    String result = cacheKey.toString();
    return result;
}

    @Override
    @CachePut(cacheNames = REGION_NAME, key = "#key", unless = "#result == null")
    public Map<String, Object> putCache(String key, Map<String, Object> valueMap) {
        return valueMap;
    }

또는
public class CacheService implements CacheImpl {

	@Override
	@CachePut(cacheNames = REGION_NAME, key = "#key", unless = "#result == null")
	public Map<String, Object> putCache(String key, Map<String, Object> valueMap) {
	    return valueMap;
	}
}
```





### 1.1 Client Proxy 종류
1) ClientCacheFactory 객체를 Spring Security, Locator 주소를 통하여 ClientCache 생성
2) ClientCache를 통해 Region을 관리 할 수 있는 ClientRegionFactoryBean 객체를 생성
3) ClientRegionFactoryBean 객체 생성 옵션 중 ClientRegionShortcut가 아래 몇가지 속성을 정의 할 수 있습니다.
	- ClientRegionShortcut.PROXY: 모든 요청을 Cache Server로 보낸다. 
	- ClientRegionShortcut.CACHING_PROXY: Local 상태가 있지만 작업을 Cache Server로 보낼 수도 있다. Local 상태를 찾을 수없는 경우 작업이 Cache Server로 전송되고 Local상태가 Cache Server 결과를 포함하도록 업데이트 된다.
	- ClientRegionShortcut.CACHING_PROXY_HEAP_LRU: CACHING_PROXY의 속성을 사용하고 Java VM Memory 부족을 감지 하면 Entry를 삭제 한다.
	- ClientRegionShortcut.CACHING_PROXY_OVERFLOW: CACHING_PROXY 속성을 사용하고 Java VM Memory이 Memory 부족을 실행하고 있음을 감지하면 entry 값을 Disk로 이동 시킨다.
	- ClientRegionShortcut.LOCAL: Local에서만 사용하고 Cache Server에는 어떠한 요청을 하지 않는다.
	- ClientRegionShortcut.LOCAL_PERSISTENT: Local을 사용하며 모든 데이터를 Disk에 써서 Java Region이 재생성 될 때마다 해당 Entry를 가져 온다.
	- ClientRegionShortcut.LOCAL_HEAP_LRU: Local를 사용하며 Java VM Memory 부족을 감지 하면 Entry를 삭제 한다.
	- ClientRegionShortcut.LOCAL_OVERFLOW: Local를 사용하며 Java VM Memory이 Memory 부족을 실행하고 있음을 감지하면 entry 값을 Disk로 이동 시킨다.
	- ClientRegionShortcut.LOCAL_PERSISTENT_OVERFLOW: Local을 사용하며 모든 자바 Region 객체가 재생성 되더라도 entry 값을 사용 할 수 있으며 Java VM Memory이 Memory 부족을 실행하고 있음을 감지하면 entry 값을 Disk로 이동 시킨다.


## 2. 특이사항
- 기본적으로 Replica 구성과 Redundant구성으로 인하여 HA 구성은 가능하지만 VM 장애로 인하여 전체 Cache Server가 내려갈 가 장애가 발생하여 모든 Region, Data가 사라질 경우를 생각하여 예외(DB에서 처리 등)를 처리 해야 할 가능성이 있습니다.
