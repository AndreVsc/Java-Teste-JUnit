package com.andrevsc.teste.gateways;

import org.springframework.stereotype.Component;

/**
 * Stub de desenvolvimento para a API do Detran.
 * Em produção, substituir por implementação real com chamada HTTP.
 */
@Component
public class DetranApiGatewayStub implements DetranApiGateway {

    @Override
    public boolean isCnhValida(String numeroCnh) {
        return numeroCnh != null && !numeroCnh.isBlank();
    }
}
