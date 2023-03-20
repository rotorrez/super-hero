package com.w2m.superhero.service;

import com.w2m.superhero.domain.Superhero;
import com.w2m.superhero.exception.SuperheroNotFoundException;
import com.w2m.superhero.repository.SuperheroRepositoryJpa;
import com.w2m.superhero.util.ExecutionTimeRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuperheroService {

    public static final String SUPERHERO = "superhero";
    private final SuperheroRepositoryJpa superheroRepository;

    public SuperheroService(SuperheroRepositoryJpa superheroRepository) {
        this.superheroRepository = superheroRepository;
    }

    @ExecutionTimeRequest
    @Cacheable(value = SUPERHERO, key = "#id")
    public Superhero getSuperheroById(Long id) {
        Superhero superheroFound = superheroRepository.findById(id)
                .orElseThrow(() -> new SuperheroNotFoundException("SuperHero with ID: " + id + " not found"));
        return superheroFound;
    }

    @ExecutionTimeRequest
    public Superhero saveSuperhero(Superhero superhero) {
        return superheroRepository.save(superhero);
    }

    @ExecutionTimeRequest
    public List<Superhero> getAllSuperhero() {
        return superheroRepository.findAll();
    }

    @ExecutionTimeRequest
    @CacheEvict(cacheNames = SUPERHERO, allEntries = true)
    public Superhero updateSuperhero(Superhero superhero, Long id) {
        Superhero superheroFound = superheroRepository.findById(id)
                .orElseThrow(() -> new SuperheroNotFoundException("SuperHero with ID: " + id + " not found to Update"));

        superheroFound.setName(superhero.getName());
        return superheroRepository.save(superheroFound);
    }

    @ExecutionTimeRequest
    @CacheEvict(cacheNames = SUPERHERO, allEntries = true)
    public void deleteSuperheroById(Long id) {
        Superhero superheroFound = superheroRepository.findById(id)
                .orElseThrow(() -> new SuperheroNotFoundException("SuperHero with ID: " + id + " not found to Delete"));
        superheroRepository.deleteById(superheroFound.getId());
    }

    @ExecutionTimeRequest
    @Cacheable(value = SUPERHERO, key = "#name")
    public List<Superhero> findBySuperheroName(String name) {
        List<Superhero> superheroesFound = superheroRepository.findByNameContainingIgnoreCase(name);
        if (superheroesFound.isEmpty()) {
            throw new SuperheroNotFoundException("Superheros containing name: " + name + " not found");
        }

        return superheroesFound;
    }

}
