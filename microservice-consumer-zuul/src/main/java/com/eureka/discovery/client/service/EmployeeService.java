package com.eureka.discovery.client.service;

import java.util.List;

import com.eureka.discovery.client.vo.Employee;
public interface EmployeeService {

	public List<Employee> getEmployeeList();
	
	public Employee findEmployeeById(Integer id);
}
