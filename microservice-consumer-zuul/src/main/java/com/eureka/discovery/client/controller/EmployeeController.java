package com.eureka.discovery.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eureka.discovery.client.service.EmployeeService;

/**
 * 
 * @author gaugupta
 *
 */

@Controller
public class EmployeeController {
	
	@Autowired
	EmployeeService employeeService;
	
	@RequestMapping("/")
	public String home(){
		return "index";
	}
	
	@RequestMapping("/employeeList")
	public String getEmployeeList(Model model) {
		model.addAttribute("employees",employeeService.getEmployeeList());
		return "employeeList";
	}
	
	@RequestMapping("/employeeDetails")
	public String getEmployeeDetails(@RequestParam(name="id") Integer id,Model model) {
		model.addAttribute("employee",employeeService.findEmployeeById(id));
		return "employeeDetails";
	}
	
}
