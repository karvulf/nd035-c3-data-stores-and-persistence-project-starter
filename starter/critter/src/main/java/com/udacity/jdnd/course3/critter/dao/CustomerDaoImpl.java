package com.udacity.jdnd.course3.critter.dao;

import com.udacity.jdnd.course3.critter.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Transactional
public class CustomerDaoImpl implements CustomerDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL_CUSTOMER =
            "SELECT * FROM customer";

    private static final String SELECT_OWNER_BY_PET_ID =
            "SELECT * FROM customer " +
                    "WHERE pet_id = :id";

    private static final String SELECT_CUSTOMER_BY_ID =
            "SELECT * FROM customer " +
                    "WHERE id = :id";

    private static final RowMapper<Customer> customerRowMapper =
            new BeanPropertyRowMapper<>(Customer.class);

    @Override
    public List<Customer> list() {
        return jdbcTemplate.query(
                SELECT_ALL_CUSTOMER,
                ((rs, rowNum) -> getCustomerFromRS(rs))
        );
    }

    @Override
    public Customer getOwnerByPet(Long petId) {
        Customer customer = jdbcTemplate.queryForObject(
                SELECT_OWNER_BY_PET_ID,
                new MapSqlParameterSource().addValue("id", petId),
                new BeanPropertyRowMapper<>(Customer.class));
        return customer;
    }

    @Override
    public Long addCustomer(Customer customer) {
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("customer")
                .usingGeneratedKeyColumns("id");
        return sji.executeAndReturnKey(new
                BeanPropertySqlParameterSource(customer)).longValue();
    }

    @Override
    public Customer getCustomerById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    SELECT_CUSTOMER_BY_ID,
                    new MapSqlParameterSource().addValue("id", id),
                    new BeanPropertyRowMapper<>(Customer.class));
        } catch (Exception e) {
            return null;
        }
    }

    private Customer getCustomerFromRS(ResultSet rs) throws SQLException {
        Customer customer = new Customer(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("phone_number"),
                rs.getString("notes")
        );
        return customer;
    }
}
