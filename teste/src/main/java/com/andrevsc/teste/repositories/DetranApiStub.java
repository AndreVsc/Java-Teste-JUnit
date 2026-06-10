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
    public boolean isCnhValida(String numeroCnh, String testScenario) {
        // Stub: considera qualquer CNH válida, exceto casos específicos de teste.
        if (numeroCnh == null || numeroCnh.isBlank()) {
            return false;
        }

        if ("ct01".equalsIgnoreCase(testScenario)
                || "ct01-cnh-principal-invalida".equalsIgnoreCase(testScenario)) {
            if (numeroCnh.equals("CNH_CLIENTE")) {
                return false; // CT01: CNH do condutor principal inválida
            }
        }

        if (numeroCnh.equals("CNH_CLIENTE_INVALIDA")) {
            return false; // CT01 (alternativo): cliente com CNH inválida explícita
        }

        if (numeroCnh.equals("CNH_LUCAS")) {
            if ("ct02-outro-condutor-invalido".equalsIgnoreCase(testScenario)
                    || "ct02".equalsIgnoreCase(testScenario)) {
                return false; // CT02: outro condutor inválido
            }
        }

        return true;
    }
}