package com.andrevsc.teste.controllers;

import com.andrevsc.teste.dtos.PagamentoDTO;
import com.andrevsc.teste.dtos.ReservaRequestDTO;
import com.andrevsc.teste.dtos.ReservaResponseDTO;
import com.andrevsc.teste.exceptions.CarroIndisponivelException;
import com.andrevsc.teste.exceptions.PagamentoRecusadoException;
import com.andrevsc.teste.models.enums.FormaPagamento;
import com.andrevsc.teste.models.enums.StatusReserva;
import com.andrevsc.teste.services.ReservaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // NOVO SB4: substituiu @MockBean
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // NOVO SB4: pacote mudou

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integração do endpoint POST /api/reservas.
 *
 * @SpringBootTest: sobe o contexto Spring completo.
 * @AutoConfigureMockMvc: habilita MockMvc (obrigatório no SB4 — não é mais automático).
 *
 * @MockitoBean (SB4): substitui @MockBean. Registra um mock Mockito no contexto
 *   Spring, permitindo controlar o comportamento do service sem executar
 *   a lógica de negócio real. Isolamos assim o controller do service.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ReservaController — integração HTTP")
class ReservaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    // MOCK no contexto Spring (SB4: @MockitoBean substituiu @MockBean)
    @MockitoBean
    private ReservaService reservaService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    }

    private ReservaRequestDTO requestValido() {
        ReservaRequestDTO r = new ReservaRequestDTO();
        r.setClienteId("cliente-01");
        r.setCarroId("carro-01");
        r.setDataInicio(LocalDate.of(2025, 6, 1));
        r.setDataFim(LocalDate.of(2025, 6, 5));
        r.setPagamento(new PagamentoDTO(FormaPagamento.PIX, 1));
        return r;
    }

    private ReservaResponseDTO responseConfirmado() {
        return new ReservaResponseDTO(
            UUID.randomUUID().toString(),
            StatusReserva.RESERVA_CONFIRMADA,
            720.0,
            "https://reservas.example.com/confirmar/abc",
            "Reserva confirmada!"
        );
    }

    @Test
    @DisplayName("POST /api/reservas com dados válidos retorna 201 Created")
    void postValido_retorna201() throws Exception {
        when(reservaService.reservarCarro(any())).thenReturn(responseConfirmado());

        mockMvc.perform(post("/api/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestValido())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("RESERVA_CONFIRMADA"))
            .andExpect(jsonPath("$.valorFinal").value(720.0))
            .andExpect(jsonPath("$.linkConfirmacao").exists());
    }

    @Test
    @DisplayName("POST com carro indisponível retorna 409 Conflict")
    void carroIndisponivel_retorna409() throws Exception {
        when(reservaService.reservarCarro(any()))
            .thenThrow(new CarroIndisponivelException("Carro indisponível"));

        mockMvc.perform(post("/api/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestValido())))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.erro").value("Carro indisponível"));
    }

    @Test
    @DisplayName("POST com pagamento recusado retorna 402 Payment Required")
    void pagamentoRecusado_retorna402() throws Exception {
        when(reservaService.reservarCarro(any()))
            .thenThrow(new PagamentoRecusadoException("Recusado pela operadora"));

        mockMvc.perform(post("/api/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestValido())))
            .andExpect(status().isPaymentRequired())
            .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("POST com dados inválidos retorna 400 Bad Request")
    void dadosInvalidos_retorna400() throws Exception {
        when(reservaService.reservarCarro(any()))
            .thenThrow(new IllegalArgumentException("clienteId é obrigatório."));

        mockMvc.perform(post("/api/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestValido())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erro").value("clienteId é obrigatório."));
    }
}