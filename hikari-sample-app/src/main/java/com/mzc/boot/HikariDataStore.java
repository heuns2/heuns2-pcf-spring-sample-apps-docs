package com.mzc.boot;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
@EnableJpaAuditing
public class HikariDataStore {
	@Value("${spring.application.name}")
	private String applicationName;
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;
	@Value("${spring.datasource.driver-class-name:oracle.jdbc.OracleDriver}") 
	private String driverClassName;
	@Value("${spring.datasource.hikari.maximum-pool-size}") 
	private String maxPoolSize;
	@Value("${spring.datasource.hikari.minimum-idle}") 
	private String minimumIdle;
	    
	@Bean(name = "dataSource")
	@Primary
	public DataSource primaryDataSource() {
	    HikariConfig config = new HikariConfig();
	    config.setDriverClassName(driverClassName);
	    config.setJdbcUrl(url);
	    config.setUsername(username);
	    config.setPassword(password);
	    config.setMaximumPoolSize(Integer.parseInt("10"));
	    config.setMinimumIdle(Integer.parseInt(minimumIdle));
	    config.setMaxLifetime(5000);
	    config.setIdleTimeout(3000);
	    config.addDataSourceProperty("v$session.program", applicationName);
	    config.addDataSourceProperty("useUnicode", "true");
	    config.addDataSourceProperty("characterEncoding", "utf8");
	    config.setConnectionTestQuery("SELECT 1 FROM DUAL");
	    DataSource ds = new HikariDataSource(config);
	    return ds;
	}
}
