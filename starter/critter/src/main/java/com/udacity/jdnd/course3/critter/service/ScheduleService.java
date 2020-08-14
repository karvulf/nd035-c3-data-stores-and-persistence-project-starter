package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.dao.EmployeeDao;
import com.udacity.jdnd.course3.critter.dao.PetDao;
import com.udacity.jdnd.course3.critter.dao.ScheduleDao;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    ScheduleDao scheduleDao;

    @Autowired
    PetDao petDao;

    @Autowired
    EmployeeDao employeeDao;

    public List<ScheduleDTO> getAllSchedules() {
        return convertEntitiesToScheduleDTOs(scheduleDao.list());
    }

    public List<ScheduleDTO> getScheduleByPet(Long petId) {
        return convertEntitiesToScheduleDTOs(scheduleDao.getSchedulesByPet(petId));
    }

    public List<ScheduleDTO> getScheduleByEmployee(Long employeeId) {
        return convertEntitiesToScheduleDTOs(scheduleDao.getSchedulesByEmployee(employeeId));
    }

    private List<ScheduleDTO> convertEntitiesToScheduleDTOs(List<Schedule> schedules) {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for(Schedule schedule : schedules) {
            ScheduleDTO scheduleDTO = convertEntityToScheduleDTO(schedule);
            scheduleDTOS.add(scheduleDTO);
        }
        return scheduleDTOS;
    }

    public List<ScheduleDTO> getScheduleForCustomer(Long customerId) {
        return convertEntitiesToScheduleDTOs(scheduleDao.getScheduleForCustomer(customerId));
    }

    public ScheduleDTO addSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = convertScheduleDTOToEntity(scheduleDTO);
        Long id = scheduleDao.addSchedule(schedule);
        schedule.setId(id);
        return convertEntityToScheduleDTO(schedule);
    }

    private Schedule convertScheduleDTOToEntity(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);

        if(scheduleDTO.getEmployeeIds() != null) {
            List<Employee> employees = new ArrayList<>();
            for(Long id : scheduleDTO.getEmployeeIds()) {
                Employee employee = employeeDao.findEmployeeById(id);
                employees.add(employee);
            }
            schedule.setEmployees(employees);
        }

        if(scheduleDTO.getPetIds() != null) {
            List<Pet> pets = new ArrayList<>();
            for(Long id : scheduleDTO.getPetIds()) {
                Pet pet = petDao.findPetById(id);
                pets.add(pet);
            }
            schedule.setPets(pets);
        }

        return schedule;
    }

    private ScheduleDTO convertEntityToScheduleDTO(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO);

        if(schedule.getEmployees() != null) {
            List<Long> employeeIds = schedule.getEmployees().stream().map(Employee::getId).collect(Collectors.toList());
            scheduleDTO.setEmployeeIds(employeeIds);
        }

        if(schedule.getPets() != null) {
            List<Long> petIds = schedule.getPets().stream().map(Pet::getId).collect(Collectors.toList());
            scheduleDTO.setPetIds(petIds);
        }

        return scheduleDTO;
    }
}
