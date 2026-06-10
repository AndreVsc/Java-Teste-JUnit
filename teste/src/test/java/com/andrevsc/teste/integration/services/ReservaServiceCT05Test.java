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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.argThat;

@DisplayName("CT05 – pagamento recusado em parcelamento 3x")
class ReservaServiceCT05Test extends ReservaServiceCTBase {

    @Test
    void CT05_pagamentoRecusadoParcelado_deveCancelarReserva() {
        when(carroRepository.findById(CARRO_ID)).thenReturn(Optional.of(carroDisponivel()));
        when(detranApiGateway.isCnhValida(anyString())).thenReturn(true);
        when(pagamentoApiGateway.processarPagamento(any())).thenReturn(false);

        ReservaRequestDTO request = requestValido();
        request.getPagamento().setFormaPagamento(FormaPagamento.CARTAO_CREDITO_PARCELADO);
        request.getPagamento().setNumeroParcelas(3);

        assertThatThrownBy(() -> service.reservarCarro(request))
            .isInstanceOf(PagamentoRecusadoException.class);

        verify(reservaRepository).save(argThat(r -> r.getStatus() == StatusReserva.RESERVA_CANCELADA));
    }
}
