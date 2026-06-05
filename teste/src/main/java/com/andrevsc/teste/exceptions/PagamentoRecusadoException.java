package com.andrevsc.teste.exceptions;

/**
 * Lançada quando o pagamento não é aprovado pela API de Pagamento.
 * Fluxo de Exceção (6): sistema valida se o pagamento foi aprovado.
 */

public class PagamentoRecusadoException extends RuntimeException {
    public PagamentoRecusadoException(String message) { super(message); }
}