package br.ufrr.dcc.projeto.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.ufrr.dcc.projeto.excecoes.EntidadeNaoEncontradaException;
import br.ufrr.dcc.projeto.excecoes.MatriculaDuplicadaException;
import br.ufrr.dcc.projeto.excecoes.SisMatriculaException;
import br.ufrr.dcc.projeto.excecoes.VagasEsgotadasException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class TurmaTest {

    private final Disciplina disciplina = new Disciplina(1, "DCC305", "POO", 60);
    private final Professor professor = new Professor(1, "Carlos Pereira", "98765432100", "1234567");

    private Turma turma(int vagas) {
        return new Turma(1, "T01", vagas, disciplina, professor);
    }

    private Aluno aluno(int id) {
        return new Aluno(id, "Aluno " + id, "1234567890" + id, "20240" + id, "BCC");
    }

    /** Todas as threads largam juntas e tentam se matricular; devolve quantas conseguiram. */
    private int tentarEmParalelo(Turma turma, List<Aluno> alunos) throws InterruptedException {
        AtomicInteger sucessos = new AtomicInteger();
        CountDownLatch largada = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();
        for (Aluno a : alunos) {
            Thread t = new Thread(() -> {
                try {
                    largada.await();
                    turma.adicionarMatricula(a);
                    sucessos.incrementAndGet();
                } catch (SisMatriculaException e) {
                    // recusada: turma cheia ou duplicada
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads.add(t);
            t.start();
        }
        largada.countDown();
        for (Thread t : threads) {
            t.join();
        }
        return sucessos.get();
    }

    @Test
    void gettersDevolvemOsDadosDoConstrutor() {
        Turma t = turma(30);
        assertEquals(1, t.getId());
        assertEquals("T01", t.getCodigo());
        assertEquals(30, t.getTotalVagas());
        assertEquals(disciplina, t.getDisciplina());
        assertEquals(professor, t.getProfessor());
        assertEquals(30, t.getVagasDisponiveis());
    }

    @Test
    void turmaImplementaIdentificavel() {
        assertInstanceOf(Identificavel.class, turma(1));
    }

    @Test
    void matriculaAteOLimiteDeVagas() throws Exception {
        Turma t = turma(2);
        t.adicionarMatricula(aluno(1));
        t.adicionarMatricula(aluno(2));
        assertEquals(0, t.getVagasDisponiveis());
    }

    @Test
    void terceiroAlunoEmTurmaDeDuasVagasFalha() throws Exception {
        Turma t = turma(2);
        t.adicionarMatricula(aluno(1));
        t.adicionarMatricula(aluno(2));
        assertThrows(VagasEsgotadasException.class, () -> t.adicionarMatricula(aluno(3)));
        assertEquals(0, t.getVagasDisponiveis());
    }

    @Test
    void mesmoAlunoDuasVezesLancaMatriculaDuplicada() throws Exception {
        Turma t = turma(5);
        Aluno a = aluno(1);
        t.adicionarMatricula(a);
        assertThrows(MatriculaDuplicadaException.class, () -> t.adicionarMatricula(a));
        assertEquals(4, t.getVagasDisponiveis());
    }

    @Test
    void cancelarLiberaVagaParaOutroAluno() throws Exception {
        Turma t = turma(1);
        Aluno a1 = aluno(1);
        t.adicionarMatricula(a1);
        assertEquals(0, t.getVagasDisponiveis());
        t.cancelarMatricula(a1);
        assertEquals(1, t.getVagasDisponiveis());
        t.adicionarMatricula(aluno(2));
        assertEquals(0, t.getVagasDisponiveis());
    }

    @Test
    void alunoQueCancelouPodeSeMatricularDeNovo() throws Exception {
        Turma t = turma(1);
        Aluno a = aluno(1);
        t.adicionarMatricula(a);
        t.cancelarMatricula(a);
        t.adicionarMatricula(a);
        assertEquals(0, t.getVagasDisponiveis());
    }

    @Test
    void cancelarAlunoNaoMatriculadoLancaEntidadeNaoEncontrada() {
        Turma t = turma(2);
        assertThrows(EntidadeNaoEncontradaException.class, () -> t.cancelarMatricula(aluno(1)));
    }

    @Test
    void cancelarDuasVezesLancaEntidadeNaoEncontradaNaSegunda() throws Exception {
        Turma t = turma(2);
        Aluno a = aluno(1);
        t.adicionarMatricula(a);
        t.cancelarMatricula(a);
        assertThrows(EntidadeNaoEncontradaException.class, () -> t.cancelarMatricula(a));
    }

    @Test
    void alterarAListaDevolvidaNaoAlteraATurma() throws Exception {
        Turma t = turma(3);
        t.adicionarMatricula(aluno(1));
        t.getMatriculas().clear();
        assertEquals(1, t.getMatriculas().size());
        assertEquals(2, t.getVagasDisponiveis());
    }

    @Test
    void matriculaCanceladaFicaNoHistoricoMasNaoOcupaVaga() throws Exception {
        Turma t = turma(2);
        Aluno a = aluno(1);
        t.adicionarMatricula(a);
        t.cancelarMatricula(a);
        assertEquals(1, t.getMatriculas().size());
        assertFalse(t.getMatriculas().get(0).isAtiva());
        assertEquals(2, t.getVagasDisponiveis());
    }

    @Test
    void metodosCriticosSaoSynchronized() throws NoSuchMethodException {
        assertTrue(Modifier.isSynchronized(
                Turma.class.getMethod("adicionarMatricula", Aluno.class).getModifiers()));
        assertTrue(Modifier.isSynchronized(
                Turma.class.getMethod("cancelarMatricula", Aluno.class).getModifiers()));
    }

    @Test
    void concorrenciaNuncaPassaDoLimiteDeVagas() throws Exception {
        for (int rodada = 0; rodada < 30; rodada++) {
            Turma t = turma(5);
            List<Aluno> alunos = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                alunos.add(aluno(i));
            }
            int sucessos = tentarEmParalelo(t, alunos);
            assertEquals(5, sucessos);
            assertEquals(0, t.getVagasDisponiveis());
            assertEquals(5, t.getMatriculas().size());
        }
    }

    @Test
    void concorrenciaMesmoAlunoSoConsegueUmaMatricula() throws Exception {
        for (int rodada = 0; rodada < 30; rodada++) {
            Turma t = turma(50);
            Aluno a = aluno(1);
            List<Aluno> repetido = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                repetido.add(a);
            }
            assertEquals(1, tentarEmParalelo(t, repetido));
            assertEquals(1, t.getMatriculas().size());
        }
    }
}
