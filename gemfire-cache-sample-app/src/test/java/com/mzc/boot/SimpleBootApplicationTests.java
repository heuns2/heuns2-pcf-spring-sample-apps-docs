package com.mzc.boot;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.internal.cache.execute.InternalFunction;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

@SuppressWarnings("rawtypes")
@SpringBootTest
@Configuration
@EnableGemfireCaching
@EnableGemfireRepositories
public class SimpleBootApplicationTests   {
	
	

	private static final long serialVersionUID = 1L;
	private static final String SECURITY_CLIENT = "security-client-auth-init";
    private static final String SECURITY_USERNAME = "security-username";
    private static final String SECURITY_PASSWORD = "security-password";
    
    @Bean
    public ClientCache gemfireCache() {   // 포항 로컬
        ClientCacheFactory factory = new ClientCacheFactory();
        factory.addPoolLocator("172.28.106.218", 55221);
        factory.addPoolLocator("172.28.106.219", 55221);
        factory.addPoolLocator("172.28.106.220", 55221);
        factory.set(SECURITY_CLIENT, "io.pivotal.spring.cloud.service.gemfire.UserAuthInitialize.create");
        factory.set(SECURITY_USERNAME, "cluster_operator_TqFoPuteScR0k1j6uOJ9Q");
        factory.set(SECURITY_PASSWORD, "jVvA8cDgTg8Vv4QhfSx6OQ");
        return factory.create();
    }
    
    
    @SuppressWarnings("unchecked")
	@Test
    public void test() {
		ClientCache gemfire = gemfireCache();
	    Region leedhRegion = gemfire.getRegion("leedh");
	    DistributedMember member = PartitionRegionHelper.getPrimaryMemberForKey(leedhRegion, "testvalueleedh1");
	    System.out.println(member.getId().toString() + member.getName().toString());
    }

	/*
	 * @Override public void execute(FunctionContext context) { FunctionContext test
	 * = new FunctionContext(); ClientCache gemfire = gemfireCache();
	 * 
	 * 
	 * RegionFunctionContext ctx = (RegionFunctionContext) context; Region
	 * leedhRegion = gemfire.getRegion("leedh"); DistributedMember member =
	 * PartitionRegionHelper.getPrimaryMemberForKey(leedhRegion, "testvalueleedh1");
	 * System.out.println(member.getId().toString() + member.getName().toString());
	 * }
	 */

}
