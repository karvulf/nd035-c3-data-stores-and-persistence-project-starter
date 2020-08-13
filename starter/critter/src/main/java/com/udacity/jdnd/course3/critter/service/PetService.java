package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.dao.PetDao;
import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.dao.CustomerDao;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    PetDao petDao;

    @Autowired
    CustomerDao customerDao;

    public PetDTO addPet(PetDTO petDTO) {
        Pet pet = convertPetDTOToEntity(petDTO);
        long petId = petDao.addPet(pet);
        pet.setId(petId);
        return convertEntityToPetDTO(pet);
    }

    public PetDTO getPetById(Long petId) {
        Pet pet = petDao.findPetById(petId);
        return convertEntityToPetDTO(pet);
    }

    public List<PetDTO> getAllPets() {
        List<Pet> pets = petDao.list();
        return pets.stream().map(this::convertEntityToPetDTO).collect(Collectors.toList());
    }

    public List<PetDTO> getPetsByOwner(Long ownerId) {
        List<Pet> pets = petDao.getPetsByOwner(ownerId);
        return pets.stream().map(this::convertEntityToPetDTO).collect(Collectors.toList());
    }

    private Pet convertPetDTOToEntity(PetDTO petDTO) {
        Pet pet = new Pet();
        BeanUtils.copyProperties(petDTO, pet);
        Customer customer = customerDao.getCustomerById(petDTO.getOwnerId());
        if (customer == null) {
            throw new UnsupportedOperationException("Owner of pet not found!");
        }
        pet.setCustomer(customer);
        return pet;
    }

    private PetDTO convertEntityToPetDTO(Pet pet) {
        PetDTO petDTO = new PetDTO();
        BeanUtils.copyProperties(pet, petDTO);
        if (pet.getCustomer() != null) {
            petDTO.setOwnerId(pet.getCustomer().getId());
        }
        return petDTO;
    }
}
