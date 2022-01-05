package kuberix.test.client.demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "jenkins")
public interface ServiceRegisrtyInterface {
	 @RequestMapping(method = {RequestMethod.GET},path = {"/"})
	 String getClient2();
}