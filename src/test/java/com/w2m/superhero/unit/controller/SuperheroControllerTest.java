package com.w2m.superhero.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.w2m.superhero.controller.SuperheroController;
import com.w2m.superhero.domain.Superhero;
import com.w2m.superhero.exception.SuperheroExistsException;
import com.w2m.superhero.exception.SuperheroNotFoundException;
import com.w2m.superhero.repository.SuperheroRepositoryJpa;
import com.w2m.superhero.service.SuperheroService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class SuperheroControllerTest {

    @Autowired
    private SuperheroService superheroService;

    @Autowired
    private SuperheroRepositoryJpa superheroRepositoryJpa;

    private MockMvc mockMvc;
    private String urlBase;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public void beforeAll() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SuperheroController(superheroService)).build();

        urlBase = "/superheros";
    }

    @Test
    public void testSaveSuperhero_WhenCreateOneSuperman_ShouldReturnOK_Superman() throws Exception {

        Superhero superhero = new Superhero(1L, "Superman");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(urlBase)
                .content(asJsonString(superhero))
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Superman")));
    }


    @Test
    public void testGetAllSuperhero_WhenCreateTwoSuperhero_ShouldReturnOK_SupermanBatman() throws Exception {
        Superhero superhero1 = new Superhero(1L, "Superman");
        Superhero superhero2 = new Superhero(2L, "Batman");

        superheroRepositoryJpa.save(superhero1);
        superheroRepositoryJpa.save(superhero2);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(urlBase)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is("Superman")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", is("Batman")));
    }

    @Test
    public void testGetSuperheroById_WhenCreateOneSuperman_ShouldReturnOK_Superman() throws Exception {
        String superheroId = "1";
        Superhero superhero = new Superhero(1L, "Superman");

        superheroRepositoryJpa.save(superhero);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(urlBase + "/{id}", superheroId)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Superman")));
    }

    @Test
    public void testFindBySuperheroName_WhenCreateThreeSearch_K_ShouldReturnOK_Hulk() throws Exception {
        String nameSearch = "k";
        Superhero superhero4 = new Superhero(1L, "Superman");
        Superhero superhero5 = new Superhero(2L, "Batman");
        Superhero superhero6 = new Superhero(3L, "Hulk");

        superheroRepositoryJpa.save(superhero4);
        superheroRepositoryJpa.save(superhero5);
        superheroRepositoryJpa.save(superhero6);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(urlBase + "/search/{name}", nameSearch)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is("Hulk")));
    }

    @Test
    public void testUpdateSuperhero_WhenCreateOneSupermanAndUpdateNameBatman_ShouldReturnOK_Batman() throws Exception {
        String superheroId = "1";
        Superhero superhero = new Superhero(1L, "Superman");

        superheroRepositoryJpa.save(superhero);
        superhero.setName("Batman");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(urlBase + "/{id}", superheroId)
                .content(asJsonString(superhero))
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Batman")));
    }

    @Test
    public void testDeleteSuperheroById_WhenCreateOneSupermanAndDelete_ShouldReturnOK_SuperheroDeleted() throws Exception {
        String superheroId = "1";
        Superhero superhero = new Superhero(1L, "Superman");

        superheroRepositoryJpa.save(superhero);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(urlBase + "/{id}", superheroId)
                .content(asJsonString(superhero))
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", is("Superhero deleted")));
    }

    @Test
    public void testGetSuperheroById_WhenSuperheroIdNotExist_ShouldThrowSuperheroNotFoundException() throws Exception {
        String superheroIdNotExist = "999";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(urlBase + "/{id}", superheroIdNotExist)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        assertEquals(SuperheroNotFoundException.class, resultActions.andReturn().getResolvedException().getClass());
    }

    @Test
    public void testSaveSuperhero_WhenSuperheroNameExist_ShouldThrowSuperheroExistsException() throws Exception {
        Superhero superhero = new Superhero(1L, "Batman");
        superheroRepositoryJpa.save(superhero);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(urlBase)
                .content(asJsonString(superhero))
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        assertEquals(SuperheroExistsException.class, resultActions.andReturn().getResolvedException().getClass());
    }
}
