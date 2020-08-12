package com.udacity.jdnd.course3.critter.dao;

import com.udacity.jdnd.course3.critter.data.Customer;

import java.util.List;

public interface CustomerDao {
    Long addCustomer(Customer customer);
    List<Customer> list();
}
