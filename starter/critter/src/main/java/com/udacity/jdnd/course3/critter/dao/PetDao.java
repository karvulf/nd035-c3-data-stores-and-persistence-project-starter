package com.udacity.jdnd.course3.critter.dao;

import com.udacity.jdnd.course3.critter.entity.Pet;

import java.util.List;

public interface PetDao {
    Long addPet(Pet pet);
    List<Pet> list();
    Pet findPetById(Long id);
    List<Pet> getPetsByOwner(Long id);
}
