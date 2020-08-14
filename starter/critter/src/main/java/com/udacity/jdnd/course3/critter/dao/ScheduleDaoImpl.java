package com.udacity.jdnd.course3.critter.dao;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class ScheduleDaoImpl implements ScheduleDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    PetDao petDao;

    private static final String ACTIVITIES = "activities";
    private static final String SCHEDULE_ID = "schedule_id";
    private static final String ID = "id";
    private static final String EMPLOYEES_ID = "employees_id";
    private static final String PETS_ID = "pets_id";

    private static final String SELECT_ALL_SCHEDULES = "SELECT * FROM schedule";

    private static final String SELECT_SCHEDULE_BY_ID = "SELECT * FROM schedule WHERE id = :" + SCHEDULE_ID;

    private static final String SELECT_SCHEDULE_ACTIVITIES_BY_ID = "SELECT * FROM schedule_activities WHERE schedule_id = :" + SCHEDULE_ID;

    private static final String SELECT_SCHEDULE_EMPLOYEES_BY_ID = "SELECT * FROM schedule_employees WHERE schedule_id = :" + ID;

    private static final String SELECT_SCHEDULE_PETS_BY_ID = "SELECT * FROM schedule_pets WHERE schedule_id = :" + ID;

    private static final String SELECT_SCHEDULE_BY_PETS_ID = "SELECT * FROM schedule_pets WHERE pets_id = :" + ID;

    private static final String SELECT_SCHEDULE_BY_EMPLOYEE_ID = "SELECT * FROM schedule_employees WHERE employees_id = :" + ID;

    private static final String INSERT_SCHEDULE_ACTIVITIES =
            "INSERT INTO schedule_activities (schedule_id, activities) " +
                    "VALUES(:" + SCHEDULE_ID + ", :" + ACTIVITIES + ")";

    private static final String INSERT_SCHEDULE_PETS =
            "INSERT INTO schedule_pets (schedule_id, pets_id) " +
                    "VALUES(:" + SCHEDULE_ID + ", :" + PETS_ID + ")";

    private static final String INSERT_SCHEDULE_EMPLOYEES =
            "INSERT INTO schedule_employees (schedule_id, employees_id) " +
                    "VALUES(:" + SCHEDULE_ID + ", :" + EMPLOYEES_ID + ")";

    private static final BeanPropertyRowMapper<Schedule> scheduleRowMapper = new BeanPropertyRowMapper<>(Schedule.class);
    private static final BeanPropertyRowMapper<Pet> petRowMapper = new BeanPropertyRowMapper<>(Pet.class);
    private static final BeanPropertyRowMapper<Employee> employeeRowMapper = new BeanPropertyRowMapper<>(Employee.class);
    private static final BeanPropertyRowMapper<EmployeeSkill> activitiesRowMapper = new BeanPropertyRowMapper<>(EmployeeSkill.class);

    @Override
    public List<Schedule> list() {
        List<Schedule> schedules = jdbcTemplate.query(
                SELECT_ALL_SCHEDULES,
                scheduleRowMapper
        );

        for (Schedule schedule : schedules) {
            Long scheduleId = schedule.getId();
            schedule.setActivities(getActivities(scheduleId));
            schedule.setEmployees(getEmployees(scheduleId));
            schedule.setPets(getPets(scheduleId));
        }

        return schedules;
    }

    @Override
    public Long addSchedule(Schedule schedule) {
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("schedule")
                .usingGeneratedKeyColumns("id");

        Long scheduleId = sji.executeAndReturnKey(new
                BeanPropertySqlParameterSource(schedule)).longValue();

        addActivities(scheduleId, schedule.getActivities());
        addPets(scheduleId, schedule.getPets());
        addEmployees(scheduleId, schedule.getEmployees());

        return scheduleId;
    }

    @Override
    public List<Schedule> getSchedulesByPet(Long petId) {
        List<Long> scheduleIds = getIds(petId, SELECT_SCHEDULE_BY_PETS_ID, "schedule_id");
        List<Schedule> schedules = new ArrayList<>();
        for(Long id : scheduleIds) {
            schedules.add(findScheduleById(id));
        }
        return schedules;
    }

    @Override
    public List<Schedule> getSchedulesByEmployee(Long employeeId) {
        List<Long> scheduleIds = getIds(employeeId, SELECT_SCHEDULE_BY_EMPLOYEE_ID, "schedule_id");
        List<Schedule> schedules = new ArrayList<>();
        for(Long id : scheduleIds) {
            schedules.add(findScheduleById(id));
        }
        return schedules;
    }

    @Override
    public List<Schedule> getScheduleForCustomer(Long customerId) {
        List<Pet> pets = petDao.getPetsByOwner(customerId);
        List<Schedule> schedules = new ArrayList<>();
        for(Pet pet : pets) {
            List<Schedule> schedulesPet = getSchedulesByPet(pet.getId());
            schedules.addAll(schedulesPet);
        }
        return schedules;
    }

    @Override
    public Schedule findScheduleById(Long scheduleId) {
        Schedule schedule = jdbcTemplate.queryForObject(
                SELECT_SCHEDULE_BY_ID,
                new MapSqlParameterSource().addValue(SCHEDULE_ID, scheduleId),
                new BeanPropertyRowMapper<>(Schedule.class));

        schedule.setActivities(getActivities(scheduleId));
        schedule.setEmployees(getEmployees(scheduleId));
        schedule.setPets(getPets(scheduleId));

        return schedule;
    }

    private List<Pet> getPets(Long scheduleId) {
        List<Long> petIds = getIds(scheduleId, SELECT_SCHEDULE_PETS_BY_ID, "pets_id");
        List<Pet> pets = new ArrayList<>();
        for(Long id : petIds) {
            pets.add(petDao.findPetById(id));
        }
        return pets;
    }

    private List<Employee> getEmployees(Long scheduleId) {
        List<Long> employeeIds = getIds(scheduleId, SELECT_SCHEDULE_EMPLOYEES_BY_ID,"employees_id");
        List<Employee> employees = new ArrayList<>();
        for(Long id : employeeIds) {
            employees.add(employeeDao.findEmployeeById(id));
        }
        return employees;
    }

    private List<Long> getIds(Long idValue, String query, String columnName) {
        return jdbcTemplate.query(
                query,
                new MapSqlParameterSource()
                        .addValue(ID, idValue),
                resultSet -> {
                    List<Long> ids = new ArrayList<>();
                    while (resultSet.next()) {
                        Long id = resultSet.getLong(columnName);
                        if(!ids.contains(id)){
                            ids.add(id);
                        }
                    }
                    return ids;
                }
        );
    }

    private Set<EmployeeSkill> getActivities(Long scheduleId) {
        return jdbcTemplate.query(
                SELECT_SCHEDULE_ACTIVITIES_BY_ID,
                new MapSqlParameterSource()
                        .addValue(SCHEDULE_ID, scheduleId),
                resultSet -> {
                    Set<EmployeeSkill> skills = new HashSet<>();
                    while (resultSet.next()) {
                        EmployeeSkill skill = EmployeeSkill.valueOf(resultSet.getString("activities"));
                        skills.add(skill);
                    }
                    return skills;
                }
        );
    }

    private void addEmployees(Long scheduleId, List<Employee> employees) {
        if (employees != null) {
            for (Employee employee : employees) {
                jdbcTemplate.update(INSERT_SCHEDULE_EMPLOYEES,
                        new MapSqlParameterSource()
                                .addValue(SCHEDULE_ID, scheduleId)
                                .addValue(EMPLOYEES_ID, employee.getId())
                );
            }
        }
    }

    private void addPets(Long scheduleId, List<Pet> pets) {
        if (pets != null) {
            for (Pet pet : pets) {
                jdbcTemplate.update(INSERT_SCHEDULE_PETS,
                        new MapSqlParameterSource()
                                .addValue(SCHEDULE_ID, scheduleId)
                                .addValue(PETS_ID, pet.getId())
                );
            }
        }
    }

    private void addActivities(Long scheduleId, Set<EmployeeSkill> skills) {
        if (skills != null) {
            for (EmployeeSkill skill : skills) {
                jdbcTemplate.update(INSERT_SCHEDULE_ACTIVITIES,
                        new MapSqlParameterSource()
                                .addValue(SCHEDULE_ID, scheduleId)
                                .addValue(ACTIVITIES, skill.toString())
                );
            }
        }
    }
}
