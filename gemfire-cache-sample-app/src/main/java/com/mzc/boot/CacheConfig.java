package com.mzc.boot;

public class CacheConfig {

	
	@Value("${pcc.service-id:cache-service}") public String PCC_SERVICE_ID;
	
	@Bean(name = "rg_m2d") ClientRegionFactoryBean m2bRegion(@Autowired
	ClientCache gemfireCache) { return getClientRegionFactoryBean(gemfireCache,
	"rg_m2d"); }
	
	@Bean(name = "rg_m7a") ClientRegionFactoryBean m7aRegion(@Autowired
	ClientCache gemfireCache) { return getClientRegionFactoryBean(gemfireCache,
	"rg_m7a"); }
	
	@Bean(name = "rg_n1b") ClientRegionFactoryBean n1bRegion(@Autowired
	ClientCache gemfireCache) { return getClientRegionFactoryBean(gemfireCache,
	"rg_n1b"); }
	
	@Bean(name = "rg_m2h") ClientRegionFactoryBean m2hRegion(@Autowired
	ClientCache gemfireCache) { return getClientRegionFactoryBean(gemfireCache,
	"rg_m2h"); }
	
	@Bean(name = "masterregion") ClientRegionFactoryBean masterRegion(@Autowired
	ClientCache gemfireCache) { return getClientRegionFactoryBean(gemfireCache,
	"masterregion"); }
	
	@Bean(name = "cacheregion") ClientRegionFactoryBean cacheRegion(@Autowired
	ClientCache gemfireCache) { return getClientRegionFactoryBean(gemfireCache,
	"cacheregion"); }
	
	@Bean(name = "ma0kafkaregion") ClientRegionFactoryBean kafkaRegion(@Autowired
	ClientCache gemfireCache) { return getClientRegionFactoryBean(gemfireCache,
	"ma0kafkaregion"); }
	
	@Bean(name = "convertregion") ClientRegionFactoryBean
	convertRegion(@Autowired ClientCache gemfireCache) { return
	getClientRegionFactoryBean(gemfireCache, "convertregion"); }
	
	@Bean(name = "eaiFormatList") ClientRegionFactoryBean
	eaiFormatListRegion(@Autowired ClientCache gemfireCache) { return
	getClientRegionFactoryBean(gemfireCache, "eaiFormatList"); }
	
	@Bean(name = "skipTcList") ClientRegionFactoryBean
	skipTcListRegion(@Autowired ClientCache gemfireCache) { return
	getClientRegionFactoryBean(gemfireCache, "skipTcList"); }
	
	@Bean(name = "eaiStatus") ClientRegionFactoryBean eaiStatusRegion(@Autowired
	ClientCache gemfireCache) { return getClientRegionFactoryBean(gemfireCache,
	"eaiStatus"); }
	
	@Bean(name = "posMessageActivities") ClientRegionFactoryBean
	posMessageActivitiesRegion(@Autowired ClientCache gemfireCache) { return
	getClientRegionFactoryBean(gemfireCache, "posMessageActivities"); }
	
	@Bean(name = "retryCount") ClientRegionFactoryBean
	retryCountRegion(@Autowired ClientCache gemfireCache) { return
	getClientRegionFactoryBean(gemfireCache, "retryCount"); }
	
	@Bean(name = "posTransAttrParam") ClientRegionFactoryBean
	posTransAttrParamRegion(@Autowired ClientCache gemfireCache) { return
	getClientRegionFactoryBean(gemfireCache, "posTransAttrParam"); }
	
	
	@Bean(name = "interfaceParam") ClientRegionFactoryBean
	interfaceParamRegion(@Autowired ClientCache gemfireCache) { return
	getClientRegionFactoryBean(gemfireCache, "interfaceParam"); }
	
	@Bean(name = "leedh") ClientRegionFactoryBean leedhParamRegion(@Autowired
	ClientCache gemfireCache) { return getClientRegionFactoryBean(gemfireCache,
	"leedh"); }
	
	private ClientRegionFactoryBean getClientRegionFactoryBean(ClientCache
	gemfireCache, String name) { ClientRegionFactoryBean f1Region = new
	ClientRegionFactoryBean<>(); f1Region.setName(name);
	f1Region.setCache(gemfireCache); f1Region.setClose(false);
	f1Region.setShortcut(ClientRegionShortcut.PROXY);
	f1Region.setLookupEnabled(true); return f1Region; }
	
}
