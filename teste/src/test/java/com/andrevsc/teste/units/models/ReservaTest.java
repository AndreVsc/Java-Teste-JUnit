package com.andrevsc.teste.units.models;

import com.andrevsc.teste.models.Carro;
import com.andrevsc.teste.models.Cliente;
import com.andrevsc.teste.models.Reserva;
import com.andrevsc.teste.models.enums.StatusReserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes da entidade Reserva.
 *
 * RECURSO DE TESTE: nenhum mock necessário — entidade pura sem dependências externas.
 * TÉCNICA: caixa-branca, cobrindo o método calcularDias() e o estado inicial.
 */
@DisplayName("Reserva — entidade")
class ReservaTest {

    // DRIVER: cria objetos mínimos para instanciar a Reserva
    private Reserva criarReserva(LocalDate inicio, LocalDate fim) {
        Cliente cliente = new Cliente("c1", "Ana", "111", "ana@x.com", "CNH001");
        Carro carro = new Carro("car1", "Civic", true, 150.0);
        return new Reserva("r1", cliente, carro, inicio, fim);
    }

    @Test
    @DisplayName("status inicial deve ser PRE_RESERVA")
    void statusInicialDeveSerPreReserva() {
        Reserva reserva = criarReserva(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 5)
        );
        assertThat(reserva.getStatus()).isEqualTo(StatusReserva.PRE_RESERVA);
    }

    @Test
    @DisplayName("calcularDias() retorna diferença correta entre datas")
    void calcularDiasRetornaDiferencaCorreta() {
        Reserva reserva = criarReserva(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 6)
        );
        assertThat(reserva.calcularDias()).isEqualTo(5L);
    }

    @Test
    @DisplayName("calcularDias() com um único dia retorna 1")
    void calcularDiasComUmDia() {
        Reserva reserva = criarReserva(
            LocalDate.of(2025, 3, 10),
            LocalDate.of(2025, 3, 11)
        );
        assertThat(reserva.calcularDias()).isEqualTo(1L);
    }

    @Test
    @DisplayName("calcularDias() atravessando mês retorna valor correto")
    void calcularDiasAtravessandoMes() {
        Reserva reserva = criarReserva(
            LocalDate.of(2025, 1, 28),
            LocalDate.of(2025, 2, 4)
        );
        assertThat(reserva.calcularDias()).isEqualTo(7L);
    }

    @Test
    @DisplayName("outroCondutor começa nulo por padrão")
    void outroCondutorComeçaNulo() {
        Reserva reserva = criarReserva(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 3)
        );
        assertThat(reserva.getOutroCondutor()).isNull();
    }
}