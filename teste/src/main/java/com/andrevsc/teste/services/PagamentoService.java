package com.andrevsc.teste.services;

import com.andrevsc.teste.dtos.PagamentoDTO;
import com.andrevsc.teste.exceptions.PagamentoRecusadoException;
import com.andrevsc.teste.models.Pagamento;
import com.andrevsc.teste.models.enums.FormaPagamento;
import com.andrevsc.teste.repositories.PagamentoApiRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PagamentoService {

    private static final int MAX_PARCELAS = 5;

    private final PagamentoApiRepository pagamentoApiRepository;

    public PagamentoService(PagamentoApiRepository pagamentoApiRepository) {
        this.pagamentoApiRepository = pagamentoApiRepository;
    }

    // RN03: Pix → 10% desc; cartão à vista → 5% desc; parcelado → sem desconto
    private double aplicarDesconto(double valor, FormaPagamento forma) {
        return switch (forma) {
            case PIX                      -> valor * 0.90;
            case CARTAO_CREDITO_VISTA     -> valor * 0.95;
            case CARTAO_CREDITO_PARCELADO -> valor;
        };
    }

    public Pagamento processarPagamento(PagamentoDTO dto, double valorOriginal) {
        // RN02: máximo de 5 parcelas
        if (dto.getFormaPagamento() == FormaPagamento.CARTAO_CREDITO_PARCELADO
                && dto.getNumeroParcelas() > MAX_PARCELAS) {
            throw new IllegalArgumentException("Máximo de " + MAX_PARCELAS + " parcelas.");
        }

        double valorFinal = aplicarDesconto(valorOriginal, dto.getFormaPagamento());

        Pagamento pagamento = new Pagamento(
            UUID.randomUUID().toString(),
            dto.getFormaPagamento(),
            dto.getNumeroParcelas(),
            valorOriginal
        );
        pagamento.setValorFinal(valorFinal);

        if (!pagamentoApiRepository.processarPagamento(pagamento)) {
            throw new PagamentoRecusadoException("Pagamento não aprovado pela operadora.");
        }

        pagamento.setAprovado(true);
        return pagamento;
    }
}