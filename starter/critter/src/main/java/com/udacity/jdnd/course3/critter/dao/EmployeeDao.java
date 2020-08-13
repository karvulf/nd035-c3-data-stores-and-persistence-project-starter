package com.udacity.jdnd.course3.critter.dao;

import com.udacity.jdnd.course3.critter.entity.Employee;

public interface EmployeeDao {
    Long addEmployee(Employee employee);
    Employee findEmployeeById(Long id);
}
