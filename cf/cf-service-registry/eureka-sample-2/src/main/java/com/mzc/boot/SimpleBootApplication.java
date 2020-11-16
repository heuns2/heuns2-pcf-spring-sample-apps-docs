package com.mzc.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;


@SpringBootApplication
@RestController
@EnableDiscoveryClient
public class SimpleBootApplication {
	
	@Autowired
	
	private EurekaClient discoveryClient;
	
	public String serviceUrl() {
	    InstanceInfo instance = discoveryClient.getNextServerFromEureka("TEST-BOOT-APP-CLIENT1", false);
	    
	    System.out.println(instance.getAppName());
	    System.out.println(instance.getMetadata());
	    System.out.println(instance.getHomePageUrl());
	    System.out.println(instance.getIPAddr());
	    System.out.println(instance.getPort());
	    
	    return instance.getHomePageUrl();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SimpleBootApplication.class, args);
	}
	
	@GetMapping("/getClient1")
	public String getClient1(){
		String clientUrl = serviceUrl();
		System.out.println(clientUrl);
		RestTemplate rest = new RestTemplate();
		return rest.getForObject(clientUrl, String.class);
	}

	@GetMapping("/")
	public String main(){
		return "I Eureka client 2";
	}

}


