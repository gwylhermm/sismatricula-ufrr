package br.ufrr.dcc.projeto.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MatriculaTest {

    private final Aluno aluno = new Aluno(1, "Maria Lima", "12345678901", "2024001", "BCC");

    @Test
    void matriculaNovaNasceAtiva() {
        assertTrue(new Matricula(aluno).isAtiva());
    }

    @Test
    void dataDaMatriculaEHoje() {
        LocalDate antes = LocalDate.now();
        Matricula m = new Matricula(aluno);
        LocalDate depois = LocalDate.now();
        assertFalse(m.getDataMatricula().isBefore(antes));
        assertFalse(m.getDataMatricula().isAfter(depois));
    }

    @Test
    void cancelarTornaAMatriculaInativa() {
        Matricula m = new Matricula(aluno);
        m.cancelar();
        assertFalse(m.isAtiva());
    }

    @Test
    void cancelarDuasVezesContinuaInativa() {
        Matricula m = new Matricula(aluno);
        m.cancelar();
        m.cancelar();
        assertFalse(m.isAtiva());
    }

    @Test
    void getAlunoDevolveOAlunoDaMatricula() {
        assertEquals(aluno, new Matricula(aluno).getAluno());
    }

    @Test
    void naoExisteSetterParaTrocarOAluno() {
        assertThrows(NoSuchMethodException.class,
                () -> Matricula.class.getMethod("setAluno", Aluno.class));
    }
}
