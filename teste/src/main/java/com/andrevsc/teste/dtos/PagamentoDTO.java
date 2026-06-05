package com.andrevsc.teste.dtos;

import com.andrevsc.teste.models.enums.FormaPagamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoDTO {
    private FormaPagamento formaPagamento;
    private int numeroParcelas;
}