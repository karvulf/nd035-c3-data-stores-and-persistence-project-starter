package com.udacity.jdnd.course3.critter.dao;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;

@Repository
@Transactional
public class EmployeeDaoImpl implements EmployeeDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private static final String NAME = "name";
    private static final String SKILLS = "skills";
    private static final String DAYS_AVAILABLE = "days_available";
    private static final String EMPLOYEE_ID = "employee_id";
    private static final String FILTERED_SKILL_TABLE = "FILTERED_SKILL_TABLE";

    private static final String DELETE_EMPLOYEE_SKILLS =
            "DELETE FROM employee_skills WHERE employee_id = :" + EMPLOYEE_ID;

    private static final String DELETE_EMPLOYEE_DAYS_AVAILABLE =
            "DELETE FROM employee_days_available WHERE employee_id = :" + EMPLOYEE_ID;

    private static final String SELECT_EMPLOYEE_BY_ID =
            "SELECT * FROM employee " +
                    "WHERE id = :id";

    private static final String INSERT_EMPLOYEE_SKILLS =
            "INSERT INTO employee_skills (employee_id, skills) " +
                    "VALUES(:" + EMPLOYEE_ID + ", :" + SKILLS + ")";

    private static final String SELECT_EMPLOYEE_SKILLS =
            "SELECT skills FROM employee_skills WHERE employee_id = :" + EMPLOYEE_ID;

    private static final String INSERT_EMPLOYEE_DAYS_AVAILABLE =
            "INSERT INTO employee_days_available (employee_id, days_available) " +
                    "VALUES(:" + EMPLOYEE_ID + ", :" + DAYS_AVAILABLE + ")";

    private static final String SELECT_EMPLOYEE_DAYS_AVAILABLE =
            "SELECT days_available FROM employee_days_available WHERE employee_id = :" + EMPLOYEE_ID;

    private static final String SELECT_EMPLOYEE_IDS_BY_DAYS_AVAILABLE_AND_SKILLS =
            "SELECT skill.employee_id FROM (";

    private static final RowMapper<Employee> employeeRowMapper =
            new BeanPropertyRowMapper<>(Employee.class);

    @Override
    public Employee findEmployeeById(Long id) {
        Employee employee = jdbcTemplate.queryForObject(
                SELECT_EMPLOYEE_BY_ID,
                new MapSqlParameterSource().addValue("id", id),
                new BeanPropertyRowMapper<>(Employee.class));
        employee.setSkills(getSkills(id));
        employee.setDaysAvailable(getDays(id));
        return employee;
    }

    @Override
    public Long addEmployee(Employee employee) {
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("employee")
                .usingGeneratedKeyColumns("id");
        Long employeeId = sji.executeAndReturnKey(new
                BeanPropertySqlParameterSource(employee)).longValue();

        // add skills to employee_skills
        addSkills(employeeId, employee.getSkills());

        // add daysAvailable to employee_days_available
        addDaysAvailable(employeeId, employee.getDaysAvailable());

        return employeeId;
    }

    @Override
    public void updateEmployee(Employee employee) {
        Long employeeId = employee.getId();
        // update skills
        deleteSkills(employeeId);
        addSkills(employeeId, employee.getSkills());

        // update days available
        deleteDaysAvailable(employeeId);
        addDaysAvailable(employeeId, employee.getDaysAvailable());
    }

    @Override
    public List<Long> findEmployeesForService(Set<EmployeeSkill> skills, DayOfWeek day) {
        String filteredSkillTable = "SELECT employee_id FROM employee_skills";
        if (skills != null && skills.size() > 0) {
            int index = 0;
            for (EmployeeSkill skill : skills) {
                if (index == 0) {
                    filteredSkillTable += " WHERE skills = '" + skill.toString() + "'";
                } else {
                    filteredSkillTable += " OR skills = '" + skill.toString() + "'";
                }
                index++;
            }
            filteredSkillTable += " GROUP BY employee_id HAVING count(employee_id)>" + (skills.size() - 1);
        }
        String query = SELECT_EMPLOYEE_IDS_BY_DAYS_AVAILABLE_AND_SKILLS + filteredSkillTable +
                ") skill INNER JOIN employee_days_available d " +
                "ON d.employee_id = skill.employee_id WHERE d.days_available = :" + DAYS_AVAILABLE;
        try {
            return jdbcTemplate.query(
                    query,
                    new MapSqlParameterSource()
                            .addValue(DAYS_AVAILABLE, day.toString()),
                    resultSet -> {
                        List<Long> ids = new ArrayList<>();
                        while (resultSet.next()) {
                            Long id = resultSet.getLong("employee_id");
                            if (!ids.contains(id)) {
                                ids.add(id);
                            }
                        }
                        return ids;
                    }
            );
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void deleteSkills(Long employeeId) {
        jdbcTemplate.update(
                DELETE_EMPLOYEE_SKILLS,
                new MapSqlParameterSource()
                        .addValue(EMPLOYEE_ID, employeeId));
    }

    private void addSkills(Long employeeId, Set<EmployeeSkill> skills) {
        if (skills != null) {
            for (EmployeeSkill skill : skills) {
                jdbcTemplate.update(INSERT_EMPLOYEE_SKILLS,
                        new MapSqlParameterSource()
                                .addValue(EMPLOYEE_ID, employeeId)
                                .addValue(SKILLS, skill.toString())
                );
            }
        }
    }

    private void deleteDaysAvailable(Long employeeId) {
        jdbcTemplate.update(
                DELETE_EMPLOYEE_DAYS_AVAILABLE,
                new MapSqlParameterSource()
                        .addValue(EMPLOYEE_ID, employeeId));
    }

    private void addDaysAvailable(Long employeeId, Set<DayOfWeek> daysAvailable) {
        if (daysAvailable != null) {
            for (DayOfWeek day : daysAvailable) {
                jdbcTemplate.update(INSERT_EMPLOYEE_DAYS_AVAILABLE,
                        new MapSqlParameterSource()
                                .addValue(EMPLOYEE_ID, employeeId)
                                .addValue(DAYS_AVAILABLE, day.toString())
                );
            }
        }
    }

    private Set<EmployeeSkill> getSkills(Long employeeId) {
        return jdbcTemplate.query(
                SELECT_EMPLOYEE_SKILLS,
                new MapSqlParameterSource()
                        .addValue(EMPLOYEE_ID, employeeId),
                resultSet -> {
                    Set<EmployeeSkill> skills = new HashSet<>();
                    while (resultSet.next()) {
                        EmployeeSkill skill = EmployeeSkill.valueOf(resultSet.getString("skills"));
                        skills.add(skill);
                    }
                    return skills;
                }
        );
    }

    private Set<DayOfWeek> getDays(Long employeeId) {
        return jdbcTemplate.query(
                SELECT_EMPLOYEE_DAYS_AVAILABLE,
                new MapSqlParameterSource()
                        .addValue(EMPLOYEE_ID, employeeId),
                resultSet -> {
                    Set<DayOfWeek> days = new HashSet<>();
                    while (resultSet.next()) {
                        DayOfWeek day = DayOfWeek.valueOf(resultSet.getString("days_available"));
                        days.add(day);
                    }
                    return days;
                }
        );
    }
}
