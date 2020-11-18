package com.mzc.boot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@EnableAutoConfiguration
public class SimpleBootApplication {
	
	@Autowired
    DataSource dataSource;
	

	public static void main(String[] args) {
		SpringApplication.run(SimpleBootApplication.class, args);
	}

	@GetMapping("/query")
	public String home() throws Exception{
		  try(Connection connection = dataSource.getConnection()){
	          System.out.println(connection);
	          String URL = connection.getMetaData().getURL();
	          System.out.println(URL);
	          String User = connection.getMetaData().getUserName();
	          System.out.println(User);
	          
	          Statement statement = connection.createStatement();
	          String sql = "SELECT 1 FROM DUAL";
	          ResultSet rs = null;
	          rs = statement.executeQuery(sql);
	          
	          while(rs.next()) {
	              String id = rs.getString(1);
	              return("SELECT 1 FROM DUAL ====== "+ id);
	             }
	          if (rs!=null) {
	          	   try {
	          	      rs.close();
	          	   } catch (SQLException e) {
	          	   }
	          	}
	          	if(statement!=null) {
	          	   try {
	          		   statement.close();
	          	   } catch (SQLException e) {
	          	   }
	          	}
	          	if(connection!=null) {
	          	   try {
	          		   connection.close();
	          	   } catch (SQLException e) {
	          	   }
	          	}
		  }
		  return "hi";
	}


}


