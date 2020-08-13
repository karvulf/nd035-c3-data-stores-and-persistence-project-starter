package com.udacity.jdnd.course3.critter.user;

public interface EmployeeDao {
    Long addEmployee(Employee employee);
    Employee findEmployeeById(Long id);
}
