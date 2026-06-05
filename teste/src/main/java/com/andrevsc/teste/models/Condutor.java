package com.andrevsc.teste.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Condutor {
    private String id;
    private String nome;
    private String numeroCnh;
}