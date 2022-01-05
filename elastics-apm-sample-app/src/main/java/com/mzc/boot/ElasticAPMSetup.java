package com.mzc.boot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import co.elastic.apm.attach.ElasticApmAttacher;

@Configuration
public class ElasticAPMSetup {
    private static final String SERVER_URL_KEY = "server_url";
    private static final String SERVICE_NAME_KEY = "service_name";
    private static final String SECRET_TOKEN_KEY = "secret_token";
    private static final String ENVIRONMENT_KEY = "environment";
    private static final String APPLICATION_PACKAGES_KEY = "application_packages";
    private static final String LOG_LEVEL_KEY = "log_level";

    @PostConstruct
    public void init() {

        Map<String, String> apmProps = new HashMap<>(6);
        apmProps.put(SERVER_URL_KEY, "http://apm-server.eks.leedh.cloud");
        apmProps.put(SERVICE_NAME_KEY, "test-app");
        apmProps.put(SECRET_TOKEN_KEY, "");
        apmProps.put(ENVIRONMENT_KEY, "dev");
        apmProps.put(APPLICATION_PACKAGES_KEY, "com.mzc.boot");
        apmProps.put(LOG_LEVEL_KEY, "debug");

        ElasticApmAttacher.attach(apmProps);
    }
}
