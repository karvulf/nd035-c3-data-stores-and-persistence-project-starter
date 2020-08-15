package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.dao.CustomerDao;
import com.udacity.jdnd.course3.critter.dao.PetDao;
import com.udacity.jdnd.course3.critter.entity.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    @Autowired
    PetDao petDao;

    @Autowired
    CustomerDao customerDao;

    public Pet addPet(Pet pet) {
        long petId = petDao.addPet(pet);
        pet.setId(petId);
        return pet;
    }

    public Pet getPetById(Long petId) {
        return petDao.findPetById(petId);
    }

    public List<Pet> getAllPets() {
        return petDao.list();
    }

    public List<Pet> getPetsByOwner(Long ownerId) {
        return petDao.getPetsByOwner(ownerId);
    }
}
