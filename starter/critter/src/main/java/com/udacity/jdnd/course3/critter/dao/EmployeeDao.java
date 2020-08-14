package com.udacity.jdnd.course3.critter.dao;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

public interface EmployeeDao {
    Long addEmployee(Employee employee);
    Employee findEmployeeById(Long id);
    void updateEmployee(Employee employee);
    List<Long> findEmployeesForService(Set<EmployeeSkill> skills, DayOfWeek day);
}
