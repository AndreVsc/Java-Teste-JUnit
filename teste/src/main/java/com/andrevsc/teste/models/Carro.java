package com.andrevsc.teste.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carro {
    private String id;
    private String modelo;
    private boolean disponivel;
    private double precoDiaria;
}