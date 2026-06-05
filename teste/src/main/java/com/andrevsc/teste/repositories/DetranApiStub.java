package com.andrevsc.teste.repositories;

import org.springframework.stereotype.Component;

/**
 * STUB de desenvolvimento para a API do Detran.
 *
 * Em produção, esta classe seria substituída por uma implementação
 * real que faz chamada HTTP ao serviço do Detran.
 *
 * Por ora, aprova qualquer CNH não nula para que a aplicação suba.
 */
@Component
public class DetranApiStub implements DetranApiRepository {

    @Override
    public boolean isCnhValida(String numeroCnh) {
        // Stub: sempre retorna true (CNH válida) em ambiente de desenvolvimento
        return true;
    }
}