package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.dao.PetDao;
import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import com.udacity.jdnd.course3.critter.dao.CustomerDao;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    PetDao petDao;

    public CustomerDTO addCustomer(CustomerDTO customerDTO) {
        Customer customer = convertCustomerDTOToEntity(customerDTO);
        long customerId = customerDao.addCustomer(customer);
        customer.setId(customerId);
        customerDTO = convertEntityToCustomerDTO(customer);
        return customerDTO;
    }

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerDao.list();
        return customers.stream().map(this::convertEntityToCustomerDTO).collect(Collectors.toList());
    }

    public CustomerDTO getOwnerByPet(Long petId) {
        Customer customer = petDao.findPetById(petId).getCustomer();
        return convertEntityToCustomerDTO(customer);
    }

    private Customer convertCustomerDTOToEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }

    private CustomerDTO convertEntityToCustomerDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);

        List<Pet> pets = petDao.getPetsByOwner(customer.getId());
        List<Long> petIds = pets.stream().map(pet -> pet.getId()).collect(Collectors.toList());
        customerDTO.setPetIds(petIds);

        return customerDTO;
    }
}
