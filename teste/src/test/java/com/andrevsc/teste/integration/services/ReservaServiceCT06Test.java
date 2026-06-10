package com.andrevsc.teste.integration.services;

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

@DisplayName("CT06 – reserva confirmada no parcelamento 3x com pagamento aprovado")
class ReservaServiceCT06Test extends ReservaServiceCTBase {

    @Test
    void CT06_parcelamentoAprovado_deveConfirmarReserva() {
        when(carroRepository.findById(CARRO_ID)).thenReturn(Optional.of(carroDisponivel()));
        when(detranApiGateway.isCnhValida(anyString())).thenReturn(true);
        when(pagamentoApiGateway.processarPagamento(any())).thenReturn(true);

        ReservaRequestDTO request = requestValido();
        request.getPagamento().setFormaPagamento(FormaPagamento.CARTAO_CREDITO_PARCELADO);
        request.getPagamento().setNumeroParcelas(3);

        ReservaResponseDTO response = service.reservarCarro(request);

        assertThat(response.getStatus()).isEqualTo(StatusReserva.RESERVA_CONFIRMADA);
    }
}
