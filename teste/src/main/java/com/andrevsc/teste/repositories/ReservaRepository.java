package com.andrevsc.teste.repositories;

import com.andrevsc.teste.models.Reserva;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReservaRepository {

    private final Map<String, Reserva> store = new HashMap<>();

    public Optional<Reserva> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public void save(Reserva reserva) {
        store.put(reserva.getId(), reserva);
    }

    public void clear() {
        store.clear();
    }

    public java.util.List<Reserva> findAll() {
        return new java.util.ArrayList<>(store.values());
    }
}