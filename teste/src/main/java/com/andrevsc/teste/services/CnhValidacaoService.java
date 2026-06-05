package com.andrevsc.teste.services;

import com.andrevsc.teste.exceptions.CnhInvalidaException;
import com.andrevsc.teste.repositories.DetranApiRepository;
import org.springframework.stereotype.Service;

@Service
public class CnhValidacaoService {

    private final DetranApiRepository detranApiRepository;

    public CnhValidacaoService(DetranApiRepository detranApiRepository) {
        this.detranApiRepository = detranApiRepository;
    }

    // RN01: todo condutor deve ter CNH válida (consultada via API Detran)
    public void validar(String numeroCnh, String nomeTitular) {
        if (numeroCnh == null || numeroCnh.isBlank()) {
            throw new CnhInvalidaException("CNH não informada para: " + nomeTitular);
        }
        if (!detranApiRepository.isCnhValida(numeroCnh)) {
            throw new CnhInvalidaException("CNH inválida para: " + nomeTitular);
        }
    }
}