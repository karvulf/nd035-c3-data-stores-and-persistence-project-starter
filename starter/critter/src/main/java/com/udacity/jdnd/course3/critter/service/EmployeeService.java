package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;
import com.udacity.jdnd.course3.critter.dao.EmployeeDao;
import com.udacity.jdnd.course3.critter.user.EmployeeRequestDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeService {

    @Autowired
    EmployeeDao employeeDao;

    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
        Employee employee = convertEmployeeDTOToEntity(employeeDTO);
        long employeeId = employeeDao.addEmployee(employee);
        employee.setId(employeeId);
        return convertEntityToEmployeeDTO(employee);
    }

    public EmployeeDTO getEmployee(Long employeeId) {
        Employee employee = employeeDao.findEmployeeById(employeeId);
        return convertEntityToEmployeeDTO(employee);
    }

    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        Employee employee = employeeDao.findEmployeeById(employeeId);
        employee.setDaysAvailable(daysAvailable);
        employeeDao.updateEmployee(employee);
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        List<Long> employeeIds = employeeDao.findEmployeesForService(employeeDTO.getSkills(), employeeDTO.getDate().getDayOfWeek());

        List<EmployeeDTO> employees = new ArrayList<>();
        for(Long id : employeeIds) {
            employees.add(convertEntityToEmployeeDTO(employeeDao.findEmployeeById(id)));
        }

        return employees;
    }

    private Employee convertEmployeeDTOToEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        return employee;
    }

    private Employee convertEmployeeRequestDTOToEntity(EmployeeRequestDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        return employee;
    }

    private EmployeeDTO convertEntityToEmployeeDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employee, employeeDTO);
        return employeeDTO;
    }
}
