package com.andrevsc.teste.exceptions;

/**
 * Lançada quando a CNH de qualquer condutor não é válida.
 * RN01: Todo condutor deve ter CNH válida.
 * Fluxo de Exceção (4): sistema verifica se condutor está com CNH válida.
 */
public class CnhInvalidaException extends RuntimeException {
    public CnhInvalidaException(String message) { super(message); }
}