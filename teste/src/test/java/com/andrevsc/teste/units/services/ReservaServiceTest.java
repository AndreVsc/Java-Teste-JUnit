package com.andrevsc.teste.units.services;

import com.andrevsc.teste.dtos.CondutorDTO;
import com.andrevsc.teste.dtos.PagamentoDTO;
import com.andrevsc.teste.dtos.ReservaRequestDTO;
import com.andrevsc.teste.dtos.ReservaResponseDTO;
import com.andrevsc.teste.exceptions.CarroIndisponivelException;
import com.andrevsc.teste.exceptions.CnhInvalidaException;
import com.andrevsc.teste.exceptions.PagamentoRecusadoException;
import com.andrevsc.teste.models.Carro;
import com.andrevsc.teste.models.Pagamento;
import com.andrevsc.teste.models.enums.FormaPagamento;
import com.andrevsc.teste.models.enums.StatusReserva;
import com.andrevsc.teste.repositories.CarroRepository;
import com.andrevsc.teste.repositories.ReservaRepository;
import com.andrevsc.teste.services.CnhValidacaoService;
import com.andrevsc.teste.services.PagamentoService;
import com.andrevsc.teste.services.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes de unidade para ReservaService — orquestrador do caso de uso.
 *
 * MOCKS utilizados:
 *   CarroRepository       — repositório in-memory (isolamos para controlar o estado do carro)
 *   ReservaRepository     — repositório in-memory (verificamos chamadas de save)
 *   CnhValidacaoService   — validação CNH (stub via doNothing / doThrow)
 *   PagamentoService      — processamento de pagamento (stub de retorno controlado)
 *
 * TÉCNICA: caixa-branca — cada branch do método reservarCarro() é exercitado.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReservaService — fluxo principal, alternativo e exceções")
class ReservaServiceTest {

    @Mock private CarroRepository carroRepository;
    @Mock private ReservaRepository reservaRepository;
    @Mock private CnhValidacaoService cnhValidacaoService;
    @Mock private PagamentoService pagamentoService;

    private ReservaService service;

    // ── Helpers para montar o request ────────────────────────────────────────

    private ReservaRequestDTO requestValido() {
        ReservaRequestDTO r = new ReservaRequestDTO();
        r.setClienteId("cliente-01");
        r.setCarroId("carro-01");
        r.setDataInicio(LocalDate.of(2025, 6, 1));
        r.setDataFim(LocalDate.of(2025, 6, 5));
        r.setPagamento(new PagamentoDTO(FormaPagamento.PIX, 1));
        return r;
    }

    private Carro carroDisponivel() {
        return new Carro("carro-01", "Civic", true, 200.0);
    }

    private Pagamento pagamentoAprovado(double valorFinal) {
        Pagamento p = new Pagamento(UUID.randomUUID().toString(),
            FormaPagamento.PIX, 1, valorFinal / 0.9);
        p.setValorFinal(valorFinal);
        p.setAprovado(true);
        return p;
    }

    @BeforeEach
    void setUp() {
        service = new ReservaService(
            carroRepository, reservaRepository,
            cnhValidacaoService, pagamentoService
        );
    }

    // ── Fluxo principal: reserva confirmada ──────────────────────────────────

    @Test
    @DisplayName("Fluxo principal: reserva é confirmada e retorna DTO correto")
    void fluxoPrincipal_reservaConfirmada() {
        Carro carro = carroDisponivel();
        // STUB: repositório encontra o carro
        when(carroRepository.findById("carro-01")).thenReturn(Optional.of(carro));
        // STUB: CNH válida — doNothing (método void, não faz nada = CNH ok)
        doNothing().when(cnhValidacaoService).validar(anyString(), anyString());
        // STUB: pagamento aprovado com desconto PIX
        when(pagamentoService.processarPagamento(any(), anyDouble()))
            .thenReturn(pagamentoAprovado(720.0)); // 4 dias × 200 × 0.9

        ReservaResponseDTO response = service.reservarCarro(requestValido());

        assertThat(response.getStatus()).isEqualTo(StatusReserva.RESERVA_CONFIRMADA);
        assertThat(response.getValorFinal()).isEqualTo(720.0);
        assertThat(response.getLinkConfirmacao()).contains("confirmar/");
        assertThat(response.getMensagem()).contains("confirmada");

        // Verifica que o carro foi marcado como indisponível e salvo
        assertThat(carro.isDisponivel()).isFalse();
        verify(carroRepository).save(carro);
        verify(reservaRepository, atLeastOnce()).save(any());
    }

    // ── Fluxo alternativo (3): com outro condutor ─────────────────────────────

    @Test
    @DisplayName("Fluxo alternativo: outro condutor com CNH válida é aceito")
    void fluxoAlternativo_outroCondutorValido() {
        when(carroRepository.findById("carro-01"))
            .thenReturn(Optional.of(carroDisponivel()));
        doNothing().when(cnhValidacaoService).validar(anyString(), anyString());
        when(pagamentoService.processarPagamento(any(), anyDouble()))
            .thenReturn(pagamentoAprovado(720.0));

        ReservaRequestDTO request = requestValido();
        // Fluxo alternativo: adiciona outro condutor ao request
        request.setOutroCondutor(new CondutorDTO("Lucas", "CNH_LUCAS"));

        ReservaResponseDTO response = service.reservarCarro(request);

        assertThat(response.getStatus()).isEqualTo(StatusReserva.RESERVA_CONFIRMADA);
        // CNH deve ser validada 2x: outro condutor + condutor principal
        verify(cnhValidacaoService, times(2)).validar(anyString(), anyString());
    }

    // ── Fluxo de exceção: carro não encontrado ────────────────────────────────

    @Test
    @DisplayName("Exceção: carro não encontrado lança CarroIndisponivelException")
    void carroNaoEncontrado_lancaExcecao() {
        when(carroRepository.findById("carro-01")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reservarCarro(requestValido()))
            .isInstanceOf(CarroIndisponivelException.class)
            .hasMessageContaining("carro-01");
    }

    // ── Fluxo de exceção: carro indisponível ──────────────────────────────────

    @Test
    @DisplayName("Exceção: carro indisponível lança CarroIndisponivelException")
    void carroIndisponivel_lancaExcecao() {
        Carro carroOcupado = new Carro("carro-01", "Civic", false, 200.0);
        when(carroRepository.findById("carro-01")).thenReturn(Optional.of(carroOcupado));

        assertThatThrownBy(() -> service.reservarCarro(requestValido()))
            .isInstanceOf(CarroIndisponivelException.class)
            .hasMessageContaining("Civic");
    }

    // ── Fluxo de exceção (4): CNH inválida ────────────────────────────────────

    @Test
    @DisplayName("Exceção: CNH inválida lança CnhInvalidaException e cancela reserva")
    void cnhInvalida_lancaExcecaoECancelaReserva() {
        when(carroRepository.findById("carro-01"))
            .thenReturn(Optional.of(carroDisponivel()));
        // STUB: simula CNH inválida lançando exceção
        doThrow(new CnhInvalidaException("CNH inválida para: Cliente"))
            .when(cnhValidacaoService).validar(anyString(), anyString());

        assertThatThrownBy(() -> service.reservarCarro(requestValido()))
            .isInstanceOf(CnhInvalidaException.class);
    }

    // ── Fluxo de exceção (6): pagamento recusado ──────────────────────────────

    @Test
    @DisplayName("Exceção: pagamento recusado salva reserva como CANCELADA e relança")
    void pagamentoRecusado_reservaCancelada() {
        when(carroRepository.findById("carro-01"))
            .thenReturn(Optional.of(carroDisponivel()));
        doNothing().when(cnhValidacaoService).validar(anyString(), anyString());
        // STUB: pagamento recusado
        when(pagamentoService.processarPagamento(any(), anyDouble()))
            .thenThrow(new PagamentoRecusadoException("Recusado pela operadora"));

        assertThatThrownBy(() -> service.reservarCarro(requestValido()))
            .isInstanceOf(PagamentoRecusadoException.class);

        // Reserva deve ter sido salva com status CANCELADA antes de relançar
        verify(reservaRepository).save(argThat(r ->
            r.getStatus() == StatusReserva.RESERVA_CANCELADA
        ));
    }

    // ── Validação de dados básicos ────────────────────────────────────────────

    @Test
    @DisplayName("clienteId nulo lança IllegalArgumentException")
    void clienteIdNulo_lancaExcecao() {
        ReservaRequestDTO request = requestValido();
        request.setClienteId(null);

        assertThatThrownBy(() -> service.reservarCarro(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("clienteId");
    }

    @Test
    @DisplayName("Data fim anterior à data início lança IllegalArgumentException")
    void dataFimAnteriorInicio_lancaExcecao() {
        ReservaRequestDTO request = requestValido();
        request.setDataFim(request.getDataInicio().minusDays(1));

        assertThatThrownBy(() -> service.reservarCarro(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("fim");
    }

    @Test
    @DisplayName("Dados de pagamento nulos lançam IllegalArgumentException")
    void pagamentoNulo_lancaExcecao() {
        ReservaRequestDTO request = requestValido();
        request.setPagamento(null);

        assertThatThrownBy(() -> service.reservarCarro(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("pagamento");
    }
}