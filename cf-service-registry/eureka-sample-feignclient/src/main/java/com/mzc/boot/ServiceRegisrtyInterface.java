package com.mzc.boot;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "TEST-BOOT-APP-CLIENT2")
public interface ServiceRegisrtyInterface {
	 @RequestMapping(method = {RequestMethod.GET},path = {"/"})
	 String getClient2();
}
