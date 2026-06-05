package com.andrevsc.teste.units.services;

import com.andrevsc.teste.dtos.ReservaRequestDTO;
import com.andrevsc.teste.exceptions.CnhInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CT01 – CNH inválida do condutor principal")
class ReservaServiceCT01Test extends ReservaServiceCTBase {

    @Test
    void CT01_cnhInvalidaDoCondutorPrincipal_deveLancarCnhInvalidaException() {
        when(carroRepository.findById(CARRO_ID)).thenReturn(Optional.of(carroDisponivel()));
        when(detranApiRepository.isCnhValida(anyString())).thenReturn(false);

        ReservaRequestDTO request = requestValido();

        assertThatThrownBy(() -> service.reservarCarro(request))
            .isInstanceOf(CnhInvalidaException.class)
            .hasMessageContaining("CNH inválida");

        verify(reservaRepository, never()).save(any());
    }
}
