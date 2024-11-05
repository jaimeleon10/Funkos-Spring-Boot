package org.example.funkosProject.funko.repositories;

import org.example.funkosProject.funko.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FunkoRepository extends JpaRepository<Funko, Long> {
    /*Optional<Funko> findById(Long id);*/
    Funko save(Funko funko);
    Optional<Funko> deleteFunkoById(Long id);
}