package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.service.EmployeeService;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    PetService petService;

    @Autowired
    EmployeeService employeeService;

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        Schedule schedule = convertScheduleDTOToEntity(scheduleDTO);
        schedule = scheduleService.addSchedule(schedule);
        return convertEntityToScheduleDTO(schedule);
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        return convertEntitiesToScheduleDTOs(scheduleService.getAllSchedules());
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        return convertEntitiesToScheduleDTOs(scheduleService.getScheduleByPet(petId));
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        return convertEntitiesToScheduleDTOs(scheduleService.getScheduleByEmployee(employeeId));
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        return convertEntitiesToScheduleDTOs(scheduleService.getScheduleForCustomer(customerId));
    }

    private List<ScheduleDTO> convertEntitiesToScheduleDTOs(List<Schedule> schedules) {
        return schedules.stream().map(this::convertEntityToScheduleDTO).collect(Collectors.toList());
    }

    private Schedule convertScheduleDTOToEntity(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);

        if (scheduleDTO.getEmployeeIds() != null) {
            List<Employee> employees = new ArrayList<>();
            for (Long id : scheduleDTO.getEmployeeIds()) {
                Employee employee = employeeService.getEmployee(id);
                employees.add(employee);
            }
            schedule.setEmployees(employees);
        }

        if (scheduleDTO.getPetIds() != null) {
            List<Pet> pets = new ArrayList<>();
            for (Long id : scheduleDTO.getPetIds()) {
                Pet pet = petService.getPetById(id);
                pets.add(pet);
            }
            schedule.setPets(pets);
        }

        return schedule;
    }

    private ScheduleDTO convertEntityToScheduleDTO(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO);

        if (schedule.getEmployees() != null) {
            List<Long> employeeIds = schedule.getEmployees().stream().map(Employee::getId).collect(Collectors.toList());
            scheduleDTO.setEmployeeIds(employeeIds);
        }

        if (schedule.getPets() != null) {
            List<Long> petIds = schedule.getPets().stream().map(Pet::getId).collect(Collectors.toList());
            scheduleDTO.setPetIds(petIds);
        }

        return scheduleDTO;
    }
}
