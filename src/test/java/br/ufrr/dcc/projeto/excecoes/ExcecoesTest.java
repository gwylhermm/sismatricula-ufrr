package br.ufrr.dcc.projeto.excecoes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ExcecoesTest {

    private void lancar(SisMatriculaException e) throws SisMatriculaException {
        throw e;
    }

    @Test
    void mensagemEPreservada() {
        SisMatriculaException e = new DadoInvalidoException("CPF invalido");
        assertEquals("CPF invalido", e.getMessage());
    }

    @Test
    void todasAsFilhasSaoSisMatriculaException() {
        assertInstanceOf(SisMatriculaException.class, new VagasEsgotadasException("x"));
        assertInstanceOf(SisMatriculaException.class, new MatriculaDuplicadaException("x"));
        assertInstanceOf(SisMatriculaException.class, new DadoInvalidoException("x"));
        assertInstanceOf(SisMatriculaException.class, new EntidadeNaoEncontradaException("x"));
    }

    @Test
    void raizCapturaQualquerFilha() {
        assertThrows(SisMatriculaException.class,
                () -> lancar(new VagasEsgotadasException("turma cheia")));
        assertThrows(SisMatriculaException.class,
                () -> lancar(new EntidadeNaoEncontradaException("id 99")));
    }

    @Test
    void sisMatriculaExceptionEChecked() {
        assertInstanceOf(Exception.class, new SisMatriculaException("x"));
        assertFalse(RuntimeException.class.isAssignableFrom(SisMatriculaException.class));
    }
}
