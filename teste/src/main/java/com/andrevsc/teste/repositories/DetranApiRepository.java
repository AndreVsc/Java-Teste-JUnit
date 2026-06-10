package com.andrevsc.teste.repositories;

public interface DetranApiRepository {
    boolean isCnhValida(String numeroCnh, String testScenario);

    default boolean isCnhValida(String numeroCnh) {
        return isCnhValida(numeroCnh, null);
    }
}