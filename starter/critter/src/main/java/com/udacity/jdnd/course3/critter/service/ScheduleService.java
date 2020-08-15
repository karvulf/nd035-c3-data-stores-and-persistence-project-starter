package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.dao.EmployeeDao;
import com.udacity.jdnd.course3.critter.dao.PetDao;
import com.udacity.jdnd.course3.critter.dao.ScheduleDao;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    ScheduleDao scheduleDao;

    @Autowired
    PetDao petDao;

    @Autowired
    EmployeeDao employeeDao;

    public List<Schedule> getAllSchedules() {
        return scheduleDao.list();
    }

    public List<Schedule> getScheduleByPet(Long petId) {
        return scheduleDao.getSchedulesByPet(petId);
    }

    public List<Schedule> getScheduleByEmployee(Long employeeId) {
        return scheduleDao.getSchedulesByEmployee(employeeId);
    }

    public List<Schedule> getScheduleForCustomer(Long customerId) {
        return scheduleDao.getScheduleForCustomer(customerId);
    }

    public Schedule addSchedule(Schedule schedule) {
        Long id = scheduleDao.addSchedule(schedule);
        schedule.setId(id);
        return schedule;
    }
}
