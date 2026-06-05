package com.andrevsc.teste.services;

import com.andrevsc.teste.dtos.PagamentoDTO;
import com.andrevsc.teste.exceptions.PagamentoRecusadoException;
import com.andrevsc.teste.models.Pagamento;
import com.andrevsc.teste.models.enums.FormaPagamento;
import com.andrevsc.teste.repositories.PagamentoApiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes de unidade para PagamentoService.
 *
 * MOCK: PagamentoApiRepository — simula a operadora de pagamentos.
 *       Permite testar aprovação e recusa sem integração real.
 *
 * REGRAS COBERTAS:
 *   RN02 — pagamento pode ser PIX ou cartão em até 5 parcelas.
 *   RN03 — PIX → 10% desc; cartão à vista → 5% desc; parcelado → sem desconto.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PagamentoService — RN02 e RN03")
class PagamentoServiceTest {

    // MOCK: substitui a API de pagamento externa
    @Mock
    private PagamentoApiRepository pagamentoApiRepository;

    private PagamentoService service;

    @BeforeEach
    void setUp() {
        service = new PagamentoService(pagamentoApiRepository);
    }

    // ── RN03: descontos ────────────────────────────────────────────────────────

    @Test
    @DisplayName("PIX aplica 10% de desconto (RN03)")
    void pix_aplicaDesconto10Porcento() {
        // STUB: API aprova o pagamento
        when(pagamentoApiRepository.processarPagamento(any())).thenReturn(true);

        PagamentoDTO dto = new PagamentoDTO(FormaPagamento.PIX, 1);
        Pagamento resultado = service.processarPagamento(dto, 1000.0);

        assertThat(resultado.getValorFinal()).isEqualTo(900.0);
    }

    @Test
    @DisplayName("Cartão à vista aplica 5% de desconto (RN03)")
    void cartaoVista_aplicaDesconto5Porcento() {
        when(pagamentoApiRepository.processarPagamento(any())).thenReturn(true);

        PagamentoDTO dto = new PagamentoDTO(FormaPagamento.CARTAO_CREDITO_VISTA, 1);
        Pagamento resultado = service.processarPagamento(dto, 1000.0);

        assertThat(resultado.getValorFinal()).isEqualTo(950.0);
    }

    @Test
    @DisplayName("Cartão parcelado não aplica desconto (RN03)")
    void cartaoParcelado_semDesconto() {
        when(pagamentoApiRepository.processarPagamento(any())).thenReturn(true);

        PagamentoDTO dto = new PagamentoDTO(FormaPagamento.CARTAO_CREDITO_PARCELADO, 3);
        Pagamento resultado = service.processarPagamento(dto, 1000.0);

        assertThat(resultado.getValorFinal()).isEqualTo(1000.0);
    }

    // ── RN02: limite de parcelas ──────────────────────────────────────────────

    @Test
    @DisplayName("5 parcelas é permitido (RN02 — limite exato)")
    void cincoParcelas_permitido() {
        when(pagamentoApiRepository.processarPagamento(any())).thenReturn(true);

        PagamentoDTO dto = new PagamentoDTO(FormaPagamento.CARTAO_CREDITO_PARCELADO, 5);

        assertThatCode(() -> service.processarPagamento(dto, 1000.0))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("6 parcelas lança IllegalArgumentException (RN02 — acima do limite)")
    void seisParcelas_lancaExcecao() {
        PagamentoDTO dto = new PagamentoDTO(FormaPagamento.CARTAO_CREDITO_PARCELADO, 6);

        assertThatThrownBy(() -> service.processarPagamento(dto, 1000.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("5");

        // API não deve ser chamada se a validação falhou antes
        verify(pagamentoApiRepository, never()).processarPagamento(any());
    }

    // ── Fluxo de exceção (6): pagamento recusado ──────────────────────────────

    @Test
    @DisplayName("Pagamento recusado pela API lança PagamentoRecusadoException")
    void pagamentoRecusado_lancaExcecao() {
        // STUB: API recusa o pagamento
        when(pagamentoApiRepository.processarPagamento(any())).thenReturn(false);

        PagamentoDTO dto = new PagamentoDTO(FormaPagamento.PIX, 1);

        assertThatThrownBy(() -> service.processarPagamento(dto, 500.0))
            .isInstanceOf(PagamentoRecusadoException.class);
    }

    @Test
    @DisplayName("Pagamento aprovado: objeto retornado tem aprovado=true")
    void pagamentoAprovado_aprovadoEhTrue() {
        when(pagamentoApiRepository.processarPagamento(any())).thenReturn(true);

        PagamentoDTO dto = new PagamentoDTO(FormaPagamento.PIX, 1);
        Pagamento resultado = service.processarPagamento(dto, 200.0);

        assertThat(resultado.isAprovado()).isTrue();
    }
}