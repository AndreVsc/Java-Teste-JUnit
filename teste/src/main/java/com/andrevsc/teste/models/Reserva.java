package com.andrevsc.teste.models;

import com.andrevsc.teste.models.enums.StatusReserva;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Reserva {
    private String id;
    private Cliente cliente;
    private Condutor outroCondutor;
    private Carro carro;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double valorCalculado;
    private Pagamento pagamento;
    private StatusReserva status = StatusReserva.PRE_RESERVA;
    private String linkConfirmacao;

    public Reserva(String id, Cliente cliente, Carro carro, LocalDate dataInicio, LocalDate dataFim) {
        this.id = id;
        this.cliente = cliente;
        this.carro = carro;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public long calcularDias() {
        return dataFim.toEpochDay() - dataInicio.toEpochDay();
    }
}