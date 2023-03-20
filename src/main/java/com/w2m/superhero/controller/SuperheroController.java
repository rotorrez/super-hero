package com.w2m.superhero.controller;

import com.w2m.superhero.domain.Superhero;
import com.w2m.superhero.service.SuperheroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SuperheroController {

    private final SuperheroService superheroService;

    public SuperheroController(SuperheroService superheroService) {
        this.superheroService = superheroService;
    }


    @GetMapping("/superheros/{id}")
    public ResponseEntity<Superhero> getSuperheroById(@PathVariable("id") Long superheroId) {
        Superhero superheroFound = superheroService.getSuperheroById(superheroId);
        return ResponseEntity.ok(superheroFound);
    }

    @PostMapping("/superheros")
    public ResponseEntity<Superhero> saveSuperhero(@RequestBody Superhero superhero) {
        Superhero superheroSaved = superheroService.saveSuperhero(superhero);
        return ResponseEntity.ok(superheroSaved);
    }

    @GetMapping("/superheros")
    public ResponseEntity<List<Superhero>> getAllSuperhero() {
        return ResponseEntity.ok(superheroService.getAllSuperhero());
    }

    @PutMapping("/superheros/{id}")
    public ResponseEntity<Superhero> updateSuperhero(@RequestBody Superhero superhero, @PathVariable("id") Long id) {
        Superhero superheroUpdated = superheroService.updateSuperhero(superhero, id);
        return ResponseEntity.ok(superheroUpdated);
    }

    @DeleteMapping("/superheros/{id}")
    public ResponseEntity<?> deleteSuperheroById(@PathVariable("id") Long id) {
        superheroService.deleteSuperheroById(id);
        return ResponseEntity.ok("Superhero deleted");
    }

    @GetMapping("/superheros/search/{name}")
    public ResponseEntity<List<Superhero>> findBySuperheroName(@PathVariable("name") String name) {
        return ResponseEntity.ok(superheroService.findBySuperheroName(name));
    }
}


