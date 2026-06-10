package com.andrevsc.teste.controllers;

import com.andrevsc.teste.dtos.ReservaRequestDTO;
import com.andrevsc.teste.dtos.ReservaResponseDTO;
import com.andrevsc.teste.services.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listar() {
        return ResponseEntity.ok(
            reservaService.listarTodas().stream()
                .map(r -> new ReservaResponseDTO(
                    r.getId(),
                    r.getStatus(),
                    r.getPagamento() != null ? r.getPagamento().getValorFinal() : r.getValorCalculado(),
                    r.getLinkConfirmacao(),
                    "Reserva em status: " + r.getStatus()
                ))
                .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obter(@PathVariable String id) {
        var reserva = reservaService.obterPorId(id);
        return ResponseEntity.ok(new ReservaResponseDTO(
            reserva.getId(),
            reserva.getStatus(),
            reserva.getPagamento() != null ? reserva.getPagamento().getValorFinal() : reserva.getValorCalculado(),
            reserva.getLinkConfirmacao(),
            "Reserva em status: " + reserva.getStatus()
        ));
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> reservar(
        @RequestBody ReservaRequestDTO request,
        @RequestHeader(value = "X-Test-Scenario", required = false) String testScenario
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.reservarCarro(request, testScenario));
    }
}