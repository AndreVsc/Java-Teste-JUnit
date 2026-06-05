package com.andrevsc.teste.models;

import com.andrevsc.teste.models.enums.FormaPagamento;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Pagamento {
    private String id;
    private FormaPagamento formaPagamento;
    private int numeroParcelas;
    private double valorOriginal;
    private double valorFinal;
    private boolean aprovado;

    public Pagamento(String id, FormaPagamento formaPagamento, int numeroParcelas, double valorOriginal) {
        this.id = id;
        this.formaPagamento = formaPagamento;
        this.numeroParcelas = numeroParcelas;
        this.valorOriginal = valorOriginal;
    }
}