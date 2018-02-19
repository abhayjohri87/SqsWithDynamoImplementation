package com.eureka.discovery.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.eureka.discovery.client.service.EmployeeService;
import com.eureka.discovery.client.service.Impl.EmployeeServiceImpl;

@SpringBootApplication
//@EnableDiscoveryClient
public class Initializer {
	
	public static final String EMPLOYEE_SERVICE_URL = "http://localhost:8090/";
	
	public static void main(String[] args) {
		SpringApplication.run(Initializer.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	@Bean
	public EmployeeService accountRepository(){
		return new EmployeeServiceImpl(EMPLOYEE_SERVICE_URL);
	}
}
