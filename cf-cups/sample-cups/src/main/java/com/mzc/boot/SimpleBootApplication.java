package com.mzc.boot;

import java.util.List;

import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class SimpleBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleBootApplication.class, args);
	}

	@Value("${cf.endpoint:test}")
	String cf_endpoint;
	
	@Value("${cf.admin:test}")
	String cf_admin;
	
	@Value("${cf.admin_secret:test}")
	String cf_admin_secret;

	@GetMapping("/")
	public String main() throws Exception{
		
        DefaultConnectionContext getApiConnect = connectionContext(cf_endpoint);
        PasswordGrantTokenProvider getToken = tokenProvider(cf_admin, cf_admin_secret);
        ReactorCloudFoundryClient client = cloudFoundryClient(getApiConnect, getToken);
        Organizations orgClinet = client.organizations();
		StringBuilder sb = new StringBuilder();
		List<OrganizationResource> orgList = orgClinet.list(ListOrganizationsRequest.builder().name("").build()).block().getResources();
		sb.append(orgList);
		return sb.toString();
	}
	
    public DefaultConnectionContext connectionContext(String apiHost) {
        return DefaultConnectionContext.builder()
            .apiHost(apiHost)
            .skipSslValidation(true)
            .build();
    }
    public PasswordGrantTokenProvider tokenProvider(String username,
                                             String password) {
        return PasswordGrantTokenProvider.builder()
            .password(password)
            .username(username)
            .build();
    }
    public ReactorCloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorCloudFoundryClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();
    }
	
}


