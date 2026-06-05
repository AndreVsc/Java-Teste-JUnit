package com.andrevsc.teste.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ReservaRequestDTO {
    private String clienteId;
    private String carroId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private CondutorDTO outroCondutor;
    private PagamentoDTO pagamento;
}