package com.eureka.discovery.client.service.Impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.eureka.discovery.client.service.EmployeeService;
import com.eureka.discovery.client.vo.Employee;
public class EmployeeServiceImpl implements EmployeeService{

	@Autowired
	RestTemplate restTemplate;
	
	String url;
	
	public EmployeeServiceImpl(String url) {
		this.url = url;
	}
	
	@Override
	public List<Employee> getEmployeeList() {
//		Employee[] employeeList =  restTemplate.getForObject("http://producer/employees/", Employee[].class);
		Employee[] employeeList =  restTemplate.getForObject(url+"emp-api", Employee[].class);
		return Arrays.asList(employeeList);
	}

	@Override
	public Employee findEmployeeById(Integer id) {
		return restTemplate.getForObject(url+"emp-api/{id}", Employee.class, id);
	}

}
