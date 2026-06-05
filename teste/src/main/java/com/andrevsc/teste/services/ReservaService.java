package com.andrevsc.teste.services;

import com.andrevsc.teste.dtos.ReservaRequestDTO;
import com.andrevsc.teste.dtos.ReservaResponseDTO;
import com.andrevsc.teste.exceptions.CarroIndisponivelException;
import com.andrevsc.teste.models.*;
import com.andrevsc.teste.models.enums.StatusReserva;
import com.andrevsc.teste.repositories.CarroRepository;
import com.andrevsc.teste.repositories.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReservaService {

    private final CarroRepository carroRepository;
    private final ReservaRepository reservaRepository;
    private final CnhValidacaoService cnhValidacaoService;
    private final PagamentoService pagamentoService;

    public ReservaService(CarroRepository carroRepository,
                          ReservaRepository reservaRepository,
                          CnhValidacaoService cnhValidacaoService,
                          PagamentoService pagamentoService) {
        this.carroRepository = carroRepository;
        this.reservaRepository = reservaRepository;
        this.cnhValidacaoService = cnhValidacaoService;
        this.pagamentoService = pagamentoService;
    }

    public ReservaResponseDTO reservarCarro(ReservaRequestDTO request) {
        validarDadosBasicos(request);

        // Nó 3: verifica disponibilidade
        Carro carro = carroRepository.findById(request.getCarroId())
            .orElseThrow(() -> new CarroIndisponivelException("Carro não encontrado: " + request.getCarroId()));

        if (!carro.isDisponivel()) {
            throw new CarroIndisponivelException("Carro indisponível: " + carro.getModelo());
        }

        Cliente cliente = new Cliente(request.getClienteId(), "Cliente", "", "", "CNH_CLIENTE");

        // Nó 4: outro condutor (fluxo alternativo)
        Condutor outroCondutor = null;
        if (request.getOutroCondutor() != null) {
            outroCondutor = new Condutor(
                UUID.randomUUID().toString(),
                request.getOutroCondutor().getNome(),
                request.getOutroCondutor().getNumeroCnh()
            );
            cnhValidacaoService.validar(outroCondutor.getNumeroCnh(), outroCondutor.getNome());
        }

        // Nó 5: valida CNH do condutor principal
        cnhValidacaoService.validar(cliente.getNumeroCnh(), cliente.getNome());

        Reserva reserva = new Reserva(request.getCarroId(), cliente, carro, request.getDataInicio(), request.getDataFim());
        reserva.setOutroCondutor(outroCondutor);
        reserva.setValorCalculado(reserva.calcularDias() * carro.getPrecoDiaria());
        reserva.setStatus(StatusReserva.AGUARDANDO_PAGAMENTO);

        // Nós 6-11: pagamento com descontos (lança PagamentoRecusadoException se reprovado)
        try {
            reserva.setPagamento(pagamentoService.processarPagamento(request.getPagamento(), reserva.getValorCalculado()));
            reserva.setStatus(StatusReserva.RESERVA_CONFIRMADA);
        } catch (Exception e) {
            reserva.setStatus(StatusReserva.RESERVA_CANCELADA);
            reservaRepository.save(reserva);
            throw e;
        }

        // Nó 13: confirma reserva
        carro.setDisponivel(false);
        carroRepository.save(carro);

        String link = "https://reservas.example.com/confirmar/" + reserva.getId();
        reserva.setLinkConfirmacao(link);
        reservaRepository.save(reserva);

        return new ReservaResponseDTO(
            reserva.getId(),
            reserva.getStatus(),
            reserva.getPagamento().getValorFinal(),
            link,
            "Reserva confirmada! Link enviado para o e-mail do cliente."
        );
    }

    private void validarDadosBasicos(ReservaRequestDTO request) {
        if (request.getClienteId() == null || request.getClienteId().isBlank())
            throw new IllegalArgumentException("clienteId é obrigatório.");
        if (request.getCarroId() == null || request.getCarroId().isBlank())
            throw new IllegalArgumentException("carroId é obrigatório.");
        if (request.getDataInicio() == null || request.getDataFim() == null)
            throw new IllegalArgumentException("Datas de início e fim são obrigatórias.");
        if (!request.getDataFim().isAfter(request.getDataInicio()))
            throw new IllegalArgumentException("Data fim deve ser posterior à data início.");
        if (request.getPagamento() == null)
            throw new IllegalArgumentException("Dados de pagamento são obrigatórios.");
    }
}