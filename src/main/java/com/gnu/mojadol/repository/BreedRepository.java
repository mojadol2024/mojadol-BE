package com.gnu.mojadol.repository;

import com.gnu.mojadol.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreedRepository extends JpaRepository<Breed, String> {
    Breed findByBreedName(String breedName);
}
