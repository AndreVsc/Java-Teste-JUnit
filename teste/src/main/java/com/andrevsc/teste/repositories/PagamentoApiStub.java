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
    public boolean processarPagamento(Pagamento pagamento, String testScenario) {
        if (pagamento == null) {
            return false;
        }

        if (pagamento.getFormaPagamento() == com.andrevsc.teste.models.enums.FormaPagamento.CARTAO_CREDITO_VISTA) {
            if ("ct03".equalsIgnoreCase(testScenario)
                    || "ct03-pagamento-recusado".equalsIgnoreCase(testScenario)) {
                return false; // CT03: pagamento à vista recusado
            }
            if ("ct09".equalsIgnoreCase(testScenario)
                    || "ct09-cartao-a-vista-aprovado".equalsIgnoreCase(testScenario)) {
                return true; // CT09: cartão à vista aprovado
            }
            return false; // cartão à vista é recusado por padrão no stub
        }

        if ("ct04".equalsIgnoreCase(testScenario)
                || "ct04-pagamento-pix-recusado".equalsIgnoreCase(testScenario)) {
            if (pagamento.getFormaPagamento() == com.andrevsc.teste.models.enums.FormaPagamento.PIX) {
                return false; // CT04: PIX recusado
            }
        }

        if ("ct05".equalsIgnoreCase(testScenario)
                || "ct05-pagamento-parcelado-3x-recusado".equalsIgnoreCase(testScenario)) {
            if (pagamento.getFormaPagamento() == com.andrevsc.teste.models.enums.FormaPagamento.CARTAO_CREDITO_PARCELADO
                    && pagamento.getNumeroParcelas() == 3) {
                return false; // CT05: parcelamento 3x recusado
            }
        }

        if ("ct03-pagamento-recusado".equalsIgnoreCase(testScenario)
                || "ct03".equalsIgnoreCase(testScenario)) {
            return false;
        }

        return true;
    }
}