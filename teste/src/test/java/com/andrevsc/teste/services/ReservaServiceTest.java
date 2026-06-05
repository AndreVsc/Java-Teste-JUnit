package com.andrevsc.teste.services;

import com.andrevsc.teste.dtos.CondutorDTO;
import com.andrevsc.teste.dtos.PagamentoDTO;
import com.andrevsc.teste.dtos.ReservaRequestDTO;
import com.andrevsc.teste.models.enums.FormaPagamento;
import com.andrevsc.teste.repositories.CarroRepository;
import com.andrevsc.teste.repositories.ReservaRepository;
import com.andrevsc.teste.repositories.DetranApiRepository;
import com.andrevsc.teste.repositories.PagamentoApiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de unidade para ReservaService — validações básicas.
 *
 * MOCKS utilizados:
 *   CarroRepository       — isolado para permitir injeção do serviço.
 *   ReservaRepository     — substituído por mock para evitar gravação real.
 *   CnhValidacaoService   — dependência externa de validação CNH stubbed.
 *   PagamentoService      — dependência de pagamento stubbed.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReservaService — validações básicas")
class ReservaServiceTest {

    @Mock
    private CarroRepository carroRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private DetranApiRepository detranApiRepository;

    @Mock
    private PagamentoApiRepository pagamentoApiRepository;

    private ReservaService service;

    private static final String CARRO_ID = "carro-01";
    private static final String CLIENTE_ID = "cliente-01";
    private static final LocalDate DATA_INICIO = LocalDate.of(2025, 6, 1);
    private static final LocalDate DATA_FIM = LocalDate.of(2025, 6, 5);

    @BeforeEach
    void setUp() {
        CnhValidacaoService cnhValidacaoService = new CnhValidacaoService(detranApiRepository);
        PagamentoService pagamentoService = new PagamentoService(pagamentoApiRepository);
        service = new ReservaService(carroRepository, reservaRepository, cnhValidacaoService, pagamentoService);
    }

    private ReservaRequestDTO buildRequest(FormaPagamento forma, int parcelas, CondutorDTO outroCondutor) {
        ReservaRequestDTO request = new ReservaRequestDTO();
        request.setClienteId(CLIENTE_ID);
        request.setCarroId(CARRO_ID);
        request.setDataInicio(DATA_INICIO);
        request.setDataFim(DATA_FIM);
        request.setPagamento(new PagamentoDTO(forma, parcelas));
        request.setOutroCondutor(outroCondutor);
        return request;
    }

    private ReservaRequestDTO requestValido() {
        return buildRequest(FormaPagamento.PIX, 1, null);
    }

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
