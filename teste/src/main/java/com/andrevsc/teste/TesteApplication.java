package com.andrevsc.teste;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.andrevsc.teste.models.Carro;
import com.andrevsc.teste.repositories.CarroRepository;

@SpringBootApplication
public class TesteApplication {

	public static void main(String[] args) {
		SpringApplication.run(TesteApplication.class, args);
	}

	@Bean
	CommandLineRunner seed(CarroRepository carroRepository) {
		return args -> carroRepository.save(new Carro("carro-01", "Civic", true, 200.0));
	}
}
