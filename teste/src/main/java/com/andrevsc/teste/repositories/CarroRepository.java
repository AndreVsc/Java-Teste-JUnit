package com.andrevsc.teste.repositories;

import com.andrevsc.teste.models.Carro;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class CarroRepository {

    private final Map<String, Carro> store = new HashMap<>();

    public Optional<Carro> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public void save(Carro carro) {
        store.put(carro.getId(), carro);
    }

    public void clear() {
        store.clear();
    }
}