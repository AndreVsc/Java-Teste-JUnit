package com.andrevsc.teste.units.services;

import com.andrevsc.teste.dtos.ReservaRequestDTO;
import com.andrevsc.teste.dtos.ReservaResponseDTO;
import com.andrevsc.teste.models.enums.FormaPagamento;
import com.andrevsc.teste.models.enums.StatusReserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("CT08 – PIX aprovado aplica desconto de 10% e confirma reserva")
class ReservaServiceCT08Test extends ReservaServiceCTBase {

    @Test
    void CT08_pixAprovado_deveAplicarDescontoEConfirmarReserva() {
        when(carroRepository.findById(CARRO_ID)).thenReturn(Optional.of(carroDisponivel()));
        when(detranApiRepository.isCnhValida(anyString())).thenReturn(true);
        when(pagamentoApiRepository.processarPagamento(any())).thenReturn(true);

        ReservaRequestDTO request = requestValido();
        request.getPagamento().setFormaPagamento(FormaPagamento.PIX);

        ReservaResponseDTO response = service.reservarCarro(request);

        assertThat(response.getStatus()).isEqualTo(StatusReserva.RESERVA_CONFIRMADA);
        assertThat(response.getValorFinal()).isEqualTo(VALOR_PIX);
    }
}
