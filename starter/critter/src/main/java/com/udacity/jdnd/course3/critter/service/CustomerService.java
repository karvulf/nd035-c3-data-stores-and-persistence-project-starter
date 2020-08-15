package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.dao.CustomerDao;
import com.udacity.jdnd.course3.critter.dao.PetDao;
import com.udacity.jdnd.course3.critter.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    PetDao petDao;

    public Customer addCustomer(Customer customer) {
        long customerId = customerDao.addCustomer(customer);
        customer.setId(customerId);
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.list();
    }

    public Customer getCustomerById(Long customerId) {
        return customerDao.getCustomerById(customerId);
    }
}
