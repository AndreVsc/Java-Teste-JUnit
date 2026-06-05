package com.andrevsc.teste.dtos;

import com.andrevsc.teste.models.enums.StatusReserva;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {
    private String reservaId;
    private StatusReserva status;
    private double valorFinal;
    private String linkConfirmacao;
    private String mensagem;
}