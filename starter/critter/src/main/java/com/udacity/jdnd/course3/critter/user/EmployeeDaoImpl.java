package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.pet.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class EmployeeDaoImpl implements EmployeeDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL_EMPLOYEE =
            "SELECT * FROM employee";

    private static final String SELECT_OWNER_BY_PET_ID =
            "SELECT * FROM customer " +
                    "WHERE pet_id = :id";

    private static final String SELECT_EMPLOYEE_BY_ID =
            "SELECT * FROM employee " +
                    "WHERE id = :id";

    private static final RowMapper<Employee> employeeRowMapper =
            new BeanPropertyRowMapper<>(Employee.class);

    /*
    @Override
    public List<Customer> list() {
        return jdbcTemplate.query(SELECT_ALL_CUSTOMER, customerRowMapper);
    }

    @Override
    public Customer getOwnerByPet(Long petId) {
        return jdbcTemplate.queryForObject(
                SELECT_OWNER_BY_PET_ID,
                new MapSqlParameterSource().addValue("id", petId),
                new BeanPropertyRowMapper<>(Customer.class));
    }

     */

    @Override
    public Employee findEmployeeById(Long id) {
        return jdbcTemplate.queryForObject(
                SELECT_EMPLOYEE_BY_ID,
                new MapSqlParameterSource().addValue("id", id),
                new BeanPropertyRowMapper<>(Employee.class));
    }

    @Override
    public Long addEmployee(Employee employee) {
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("employee")
                .usingGeneratedKeyColumns("id");
        return sji.executeAndReturnKey(new
                BeanPropertySqlParameterSource(employee)).longValue();
    }
}
