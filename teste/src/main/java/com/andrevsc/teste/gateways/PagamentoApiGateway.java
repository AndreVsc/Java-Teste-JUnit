package com.andrevsc.teste.gateways;

import com.andrevsc.teste.models.Pagamento;

public interface PagamentoApiGateway {
    boolean processarPagamento(Pagamento pagamento);
}
