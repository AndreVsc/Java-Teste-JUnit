package com.andrevsc.teste.controllers;

import com.andrevsc.teste.dtos.ReservaRequestDTO;
import com.andrevsc.teste.dtos.ReservaResponseDTO;
import com.andrevsc.teste.services.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> reservar(@RequestBody ReservaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.reservarCarro(request));
    }
}