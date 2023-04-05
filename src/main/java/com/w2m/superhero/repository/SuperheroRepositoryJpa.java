package com.w2m.superhero.repository;

import com.w2m.superhero.domain.Superhero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuperheroRepositoryJpa extends JpaRepository<Superhero, Long> {
    List<Superhero> findByNameContainingIgnoreCase(String username);
    Boolean existsSuperheroesByName(String name);
}
