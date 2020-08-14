package com.udacity.jdnd.course3.critter.dao;

import com.udacity.jdnd.course3.critter.entity.Schedule;

import java.util.List;

public interface ScheduleDao {
    Long addSchedule(Schedule schedule);

    List<Schedule> list();

    List<Schedule> getSchedulesByPet(Long petId);

    Schedule findScheduleById(Long scheduleId);

    List<Schedule> getSchedulesByEmployee(Long employeeId);

    List<Schedule> getScheduleForCustomer(Long customerId);
}
