package com.mzc.boot;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;

@Configuration
@EnableGemfireCaching
public class CacheConfig {

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
}


