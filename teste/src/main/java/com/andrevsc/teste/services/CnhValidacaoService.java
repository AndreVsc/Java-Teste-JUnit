package com.andrevsc.teste.services;

import com.andrevsc.teste.exceptions.CnhInvalidaException;
import com.andrevsc.teste.gateways.DetranApiGateway;
import org.springframework.stereotype.Service;

@Service
public class CnhValidacaoService {

    private final DetranApiGateway detranApiGateway;

    public CnhValidacaoService(DetranApiGateway detranApiGateway) {
        this.detranApiGateway = detranApiGateway;
    }

    // RN01: todo condutor deve ter CNH válida (consultada via API Detran)
    public void validar(String numeroCnh, String nomeTitular) {
        if (numeroCnh == null || numeroCnh.isBlank()) {
            throw new CnhInvalidaException("CNH não informada para: " + nomeTitular);
        }
        if (!detranApiGateway.isCnhValida(numeroCnh)) {
            throw new CnhInvalidaException("CNH inválida para: " + nomeTitular);
        }
    }
}
