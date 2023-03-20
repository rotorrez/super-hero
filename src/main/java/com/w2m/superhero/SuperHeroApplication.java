package com.w2m.superhero;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Super Hero API", version = "1.0"))
@EnableCaching
public class SuperHeroApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuperHeroApplication.class, args);
    }

}
