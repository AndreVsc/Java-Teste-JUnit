package com.andrevsc.teste.services;

import com.andrevsc.teste.exceptions.CnhInvalidaException;
import com.andrevsc.teste.repositories.DetranApiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

/**
 * Testes de unidade para CnhValidacaoService.
 *
 * MOCK: DetranApiRepository — simula a API externa do Detran
 *       sem fazer nenhuma chamada de rede real.
 *       Configuramos o comportamento desejado com when(...).thenReturn(...).
 *
 * REGRA COBERTA: RN01 — todo condutor deve ter CNH válida.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CnhValidacaoService — RN01")
class CnhValidacaoServiceTest {

    // MOCK: substitui a dependência externa DetranApiRepository
    @Mock
    private DetranApiRepository detranApiRepository;

    private CnhValidacaoService service;

    @BeforeEach
    void setUp() {
        // Injeta o mock manualmente via construtor (sem Spring)
        service = new CnhValidacaoService(detranApiRepository);
    }

    @Test
    @DisplayName("CNH válida: não lança exceção")
    void cnhValida_naoLancaExcecao() {
        // STUB inline: Detran retorna true para esta CNH
        when(detranApiRepository.isCnhValida("CNH123")).thenReturn(true);

        assertThatCode(() -> service.validar("CNH123", "João"))
            .doesNotThrowAnyException();

        // Verifica que a API foi realmente consultada uma vez
        verify(detranApiRepository, times(1)).isCnhValida("CNH123");
    }

    @Test
    @DisplayName("CNH inválida: lança CnhInvalidaException")
    void cnhInvalida_lancaExcecao() {
        // STUB inline: Detran retorna false → CNH bloqueada/inválida
        when(detranApiRepository.isCnhValida("CNH_INVALIDA")).thenReturn(false);

        assertThatThrownBy(() -> service.validar("CNH_INVALIDA", "Maria"))
            .isInstanceOf(CnhInvalidaException.class)
            .hasMessageContaining("Maria");
    }

    @Test
    @DisplayName("CNH nula: lança CnhInvalidaException sem consultar o Detran")
    void cnhNula_lancaExcecaoSemConsultarApi() {
        assertThatThrownBy(() -> service.validar(null, "Carlos"))
            .isInstanceOf(CnhInvalidaException.class);

        // Garante que a API externa NÃO foi chamada (CNH nem chegou lá)
        verify(detranApiRepository, never()).isCnhValida(any());
    }

    @Test
    @DisplayName("CNH em branco: lança CnhInvalidaException sem consultar o Detran")
    void cnhEmBranco_lancaExcecaoSemConsultarApi() {
        assertThatThrownBy(() -> service.validar("   ", "Pedro"))
            .isInstanceOf(CnhInvalidaException.class);

        verify(detranApiRepository, never()).isCnhValida(any());
    }
}