package com.andrevsc.teste.integration.services;

import com.andrevsc.teste.dtos.ReservaRequestDTO;
import com.andrevsc.teste.exceptions.PagamentoRecusadoException;
import com.andrevsc.teste.models.enums.FormaPagamento;
import com.andrevsc.teste.models.enums.StatusReserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CT04 – pagamento recusado no PIX")
class ReservaServiceCT04Test extends ReservaServiceCTBase {

    @Test
    void CT04_pagamentoRecusadoPix_deveCancelarReserva() {
        when(carroRepository.findById(CARRO_ID)).thenReturn(Optional.of(carroDisponivel()));
        when(detranApiGateway.isCnhValida(anyString())).thenReturn(true);
        when(pagamentoApiGateway.processarPagamento(any())).thenReturn(false);

        ReservaRequestDTO request = requestValido();
        request.getPagamento().setFormaPagamento(FormaPagamento.PIX);

        assertThatThrownBy(() -> service.reservarCarro(request))
            .isInstanceOf(PagamentoRecusadoException.class);

        verify(reservaRepository).save(argThat(r -> r.getStatus() == StatusReserva.RESERVA_CANCELADA));
    }
}
