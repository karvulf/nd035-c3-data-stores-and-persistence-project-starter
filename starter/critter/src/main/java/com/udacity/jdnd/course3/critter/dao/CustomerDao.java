package com.udacity.jdnd.course3.critter.dao;

import com.udacity.jdnd.course3.critter.entity.Customer;

import java.util.List;

public interface CustomerDao {
    Long addCustomer(Customer customer);
    List<Customer> list();
    Customer getOwnerByPet(Long petId);
    Customer getCustomerById(Long id);
}
