package com.andrevsc.teste.repositories;

import com.andrevsc.teste.models.Pagamento;
import org.springframework.stereotype.Component;

/**
 * STUB de desenvolvimento para a API de Pagamento.
 *
 * Em produção, esta classe seria substituída por uma implementação
 * real que se comunica com a operadora de pagamentos.
 *
 * Por ora, aprova qualquer pagamento para que a aplicação suba.
 */
@Component
public class PagamentoApiStub implements PagamentoApiRepository {

    @Override
    public boolean processarPagamento(Pagamento pagamento) {
        // Stub: sempre retorna true (pagamento aprovado) em ambiente de desenvolvimento
        return true;
    }
}