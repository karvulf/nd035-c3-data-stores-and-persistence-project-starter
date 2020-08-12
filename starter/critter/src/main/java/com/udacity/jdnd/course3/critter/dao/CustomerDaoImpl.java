package com.udacity.jdnd.course3.critter.dao;

import com.udacity.jdnd.course3.critter.data.Customer;
import com.udacity.jdnd.course3.critter.data.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Repository
@Transactional
public class CustomerDaoImpl implements CustomerDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL_CUSTOMER =
            "SELECT * FROM customer";

    private static final RowMapper<Customer> customerRowMapper =
            new BeanPropertyRowMapper<>(Customer.class);

    @Override
    public List<Customer> list() {
        return jdbcTemplate.query(SELECT_ALL_CUSTOMER, customerRowMapper);
    }

    @Override
    public Long addCustomer(Customer customer) {
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("customer")
                .usingGeneratedKeyColumns("id");
        return sji.executeAndReturnKey(new
                BeanPropertySqlParameterSource(customer)).longValue();
    }
}
