package com.eureka.discovery.client.dynamodb.repositories;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.eureka.discovery.client.vo.Employee;

/**
 * Interface responsible to handle the db operation for target state entity.
 */
@EnableScan
public interface EmployeeRepository extends CrudRepository<Employee, String> {

	
}
