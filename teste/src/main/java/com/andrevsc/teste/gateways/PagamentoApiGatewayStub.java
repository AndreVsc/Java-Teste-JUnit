package com.andrevsc.teste.gateways;

import com.andrevsc.teste.models.Pagamento;
import org.springframework.stereotype.Component;

/**
 * Stub de desenvolvimento para a API de Pagamento.
 * Em produção, substituir por implementação real com chamada HTTP.
 */
@Component
public class PagamentoApiGatewayStub implements PagamentoApiGateway {

    @Override
    public boolean processarPagamento(Pagamento pagamento) {
        return pagamento != null;
    }
}
