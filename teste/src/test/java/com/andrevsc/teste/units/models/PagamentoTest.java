package com.andrevsc.teste.units.models;

import com.andrevsc.teste.models.Pagamento;
import com.andrevsc.teste.models.enums.FormaPagamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes da entidade Pagamento.
 *
 * RECURSO DE TESTE: nenhum mock — entidade pura.
 * TÉCNICA: caixa-branca, cobrindo estado inicial e campos do objeto.
 */
@DisplayName("Pagamento — entidade")
class PagamentoTest {

    @Test
    @DisplayName("aprovado deve ser false ao criar o objeto")
    void aprovadoDeveSerFalseAoCriar() {
        // DRIVER: construtor parcial (sem valorFinal ainda)
        Pagamento pagamento = new Pagamento(
            "p1", FormaPagamento.PIX, 1, 500.0
        );
        assertThat(pagamento.isAprovado()).isFalse();
    }

    @Test
    @DisplayName("valorFinal pode ser definido após a criação")
    void valorFinalPodeSerDefinidoAposCriacao() {
        Pagamento pagamento = new Pagamento(
            "p1", FormaPagamento.PIX, 1, 500.0
        );
        pagamento.setValorFinal(450.0);
        assertThat(pagamento.getValorFinal()).isEqualTo(450.0);
    }

    @Test
    @DisplayName("valorOriginal é preservado após setar valorFinal")
    void valorOriginalEPreservadoAposSetarFinal() {
        Pagamento pagamento = new Pagamento(
            "p1", FormaPagamento.CARTAO_CREDITO_VISTA, 1, 300.0
        );
        pagamento.setValorFinal(285.0);
        assertThat(pagamento.getValorOriginal()).isEqualTo(300.0);
    }
}