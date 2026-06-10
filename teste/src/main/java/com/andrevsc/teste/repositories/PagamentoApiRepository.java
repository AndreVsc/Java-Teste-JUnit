package com.andrevsc.teste.repositories;

import com.andrevsc.teste.models.Pagamento;

public interface PagamentoApiRepository {
    boolean processarPagamento(Pagamento pagamento, String testScenario);

    default boolean processarPagamento(Pagamento pagamento) {
        return processarPagamento(pagamento, null);
    }
}