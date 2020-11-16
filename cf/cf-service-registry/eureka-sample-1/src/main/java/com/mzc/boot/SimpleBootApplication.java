package com.mzc.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;


@SpringBootApplication
@RestController
@EnableDiscoveryClient
@EnableFeignClients
public class SimpleBootApplication {
	
	@Autowired
	private EurekaClient discoveryClient;
	
	public String serviceUrl() {
	    InstanceInfo instance = discoveryClient.getNextServerFromEureka("TEST-BOOT-APP-CLIENT2", false);
	    return instance.getHomePageUrl();
	}
	
	@GetMapping("/getClient2")
	public String getClient2(){
		
		return null;
	}

	public static void main(String[] args) {
		SpringApplication.run(SimpleBootApplication.class, args);
	}

	@GetMapping("/")
	public String main(){
		return "I Eureka client 1";
	}

}


