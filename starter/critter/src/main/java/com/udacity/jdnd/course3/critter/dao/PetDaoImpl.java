package com.udacity.jdnd.course3.critter.dao;

import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.pet.PetType;
import com.udacity.jdnd.course3.critter.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class PetDaoImpl implements PetDao {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    CustomerDao customerDao;

    private static final String BIRTH_DATE = "birthDate";
    private static final String NAME = "name";
    private static final String NOTES = "notes";
    private static final String TYPE = "type";
    private static final String OWNER_ID = "owner_id";

    private static final String SELECT_ALL_PET =
            "SELECT * FROM pet";

    private static final String SELECT_PET_BY_ID =
            "SELECT * FROM pet " +
                    "WHERE id = :id";

    private static final String SELECT_PET_BY_OWNER_ID =
            "SELECT * FROM pet " +
                    "WHERE owner_id = :id";

    private static final String INSERT_PET =
            "INSERT INTO pet (birth_date, name, notes, type, owner_id) " +
                    "VALUES(:" + BIRTH_DATE + ", :" + NAME + ", :" + NOTES + ", :" + TYPE + ", :" + OWNER_ID + ")";


    private static final RowMapper<Pet> petRowMapper =
            new BeanPropertyRowMapper<>(Pet.class);

    @Override
    public Long addPet(Pet pet) {
        Long ownerId = null;
        if (pet.getCustomer() != null) {
            ownerId = pet.getCustomer().getId();
        }
        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT_PET,
                new MapSqlParameterSource()
                        .addValue(BIRTH_DATE, pet.getBirthDate())
                        .addValue(NAME, pet.getName())
                        .addValue(NOTES, pet.getNotes())
                        .addValue(TYPE, pet.getType().toString())
                        .addValue(OWNER_ID, ownerId),
                key);
        return key.getKey().longValue();
    }

    @Override
    public List<Pet> list() {
        return jdbcTemplate.query(SELECT_ALL_PET, (rs, rowNum) -> getPetByRS(rs));
    }

    @Override
    public Pet findPetById(Long id) {
        return jdbcTemplate.queryForObject(
                SELECT_PET_BY_ID,
                new MapSqlParameterSource().addValue("id", id),
                (rs, rowNum) -> getPetByRS(rs)
        );
    }

    @Override
    public List<Pet> getPetsByOwner(Long id) {
        return jdbcTemplate.query(
                SELECT_PET_BY_OWNER_ID,
                new MapSqlParameterSource().addValue("id", id),
                (rs, rowNum) -> getPetByRS(rs));
    }

    private Customer getCustomerById(Long id) {
        if(id != null){
            return customerDao.getCustomerById(id);
        }
        return null;
    }

    private Pet getPetByRS(ResultSet rs) throws SQLException {
        LocalDate localDate = null;
        if(rs.getTimestamp("birth_date") != null) {
            localDate = rs.getTimestamp("birth_date").toLocalDateTime().toLocalDate();
        }
        return new Pet(
            rs.getLong("id"),
            PetType.valueOf(rs.getString("type")),
            rs.getString("name"),
            localDate,
            rs.getString("notes"),
            getCustomerById(rs.getLong("owner_id"))
        );
    }
}
