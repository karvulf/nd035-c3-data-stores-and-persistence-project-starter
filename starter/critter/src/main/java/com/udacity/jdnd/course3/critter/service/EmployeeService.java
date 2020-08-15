package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.dao.EmployeeDao;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeService {

    @Autowired
    EmployeeDao employeeDao;

    public Employee addEmployee(Employee employee) {
        long employeeId = employeeDao.addEmployee(employee);
        employee.setId(employeeId);
        return employee;
    }

    public Employee getEmployee(Long employeeId) {
        return employeeDao.findEmployeeById(employeeId);
    }

    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        Employee employee = employeeDao.findEmployeeById(employeeId);
        employee.setDaysAvailable(daysAvailable);
        employeeDao.updateEmployee(employee);
    }

    public List<Employee> findEmployeesForService(Set<EmployeeSkill> skills, DayOfWeek dayOfWeek) {
        List<Long> employeeIds = employeeDao.findEmployeesForService(skills, dayOfWeek);

        List<Employee> employees = new ArrayList<>();
        for (Long id : employeeIds) {
            employees.add(employeeDao.findEmployeeById(id));
        }

        return employees;
    }
}
