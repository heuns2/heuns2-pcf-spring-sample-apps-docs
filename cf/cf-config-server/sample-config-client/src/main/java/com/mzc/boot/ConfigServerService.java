package com.mzc.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Service
@RefreshScope
public class ConfigServerService {
	
	@Value("${config.profile:test}")
	String profile;
	
	@Value("${config.msg:test}")
	String message;
	
	public String configServer() {
		StringBuilder sb = new StringBuilder();
		sb.append("profile: " + profile + "</br>");
		sb.append("message: " + message + "</br>");
		return sb.toString();
	}

	
}
