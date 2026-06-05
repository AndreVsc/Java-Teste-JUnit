package com.andrevsc.teste.units.services;

import com.andrevsc.teste.dtos.CondutorDTO;
import com.andrevsc.teste.dtos.PagamentoDTO;
import com.andrevsc.teste.dtos.ReservaRequestDTO;
import com.andrevsc.teste.models.Carro;
import com.andrevsc.teste.models.enums.FormaPagamento;
import com.andrevsc.teste.repositories.CarroRepository;
import com.andrevsc.teste.repositories.ReservaRepository;
import com.andrevsc.teste.repositories.DetranApiRepository;
import com.andrevsc.teste.repositories.PagamentoApiRepository;
import com.andrevsc.teste.services.CnhValidacaoService;
import com.andrevsc.teste.services.PagamentoService;
import com.andrevsc.teste.services.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
abstract class ReservaServiceCTBase {

    @Mock
    protected CarroRepository carroRepository;

    @Mock
    protected ReservaRepository reservaRepository;

    @Mock
    protected DetranApiRepository detranApiRepository;

    @Mock
    protected PagamentoApiRepository pagamentoApiRepository;

    protected ReservaService service;

    protected static final String CARRO_ID = "carro-01";
    protected static final String CLIENTE_ID = "cliente-01";
    protected static final LocalDate DATA_INICIO = LocalDate.of(2025, 6, 1);
    protected static final LocalDate DATA_FIM = LocalDate.of(2025, 6, 5);
    protected static final double PRECO_DIARIA = 200.0;
    protected static final double VALOR_PIX = 720.0;
    protected static final double VALOR_CARTAO_VISTA = 760.0;

    @BeforeEach
    void setUp() {
        CnhValidacaoService cnhValidacaoService = new CnhValidacaoService(detranApiRepository);
        PagamentoService pagamentoService = new PagamentoService(pagamentoApiRepository);
        service = new ReservaService(carroRepository, reservaRepository, cnhValidacaoService, pagamentoService);
    }

    protected ReservaRequestDTO buildRequest(FormaPagamento forma, int parcelas, CondutorDTO outroCondutor) {
        ReservaRequestDTO request = new ReservaRequestDTO();
        request.setClienteId(CLIENTE_ID);
        request.setCarroId(CARRO_ID);
        request.setDataInicio(DATA_INICIO);
        request.setDataFim(DATA_FIM);
        request.setPagamento(new PagamentoDTO(forma, parcelas));
        request.setOutroCondutor(outroCondutor);
        return request;
    }

    protected ReservaRequestDTO requestValido() {
        return buildRequest(FormaPagamento.PIX, 1, null);
    }

    protected Carro carroDisponivel() {
        return new Carro(CARRO_ID, "Civic", true, PRECO_DIARIA);
    }
}
