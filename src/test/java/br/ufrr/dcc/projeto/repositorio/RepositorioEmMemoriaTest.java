package br.ufrr.dcc.projeto.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.ufrr.dcc.projeto.excecoes.EntidadeNaoEncontradaException;
import br.ufrr.dcc.projeto.modelo.Aluno;
import br.ufrr.dcc.projeto.modelo.Disciplina;
import br.ufrr.dcc.projeto.modelo.Identificavel;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RepositorioEmMemoriaTest {

    private Aluno aluno(int id, String nome) {
        return new Aluno(id, nome, "1234567890" + id, "20240" + id, "BCC");
    }

    @Test
    void repositorioNovoEstaVazio() {
        assertTrue(new RepositorioEmMemoria<Aluno>().listarTodos().isEmpty());
    }

    @Test
    void salvarTresItensEListarDevolveTres() {
        Repositorio<Aluno> repo = new RepositorioEmMemoria<>();
        repo.salvar(aluno(1, "Ana"));
        repo.salvar(aluno(2, "Bia"));
        repo.salvar(aluno(3, "Caio"));
        assertEquals(3, repo.listarTodos().size());
    }

    @Test
    void listarMantemAOrdemDeInsercao() {
        Repositorio<Aluno> repo = new RepositorioEmMemoria<>();
        repo.salvar(aluno(5, "Ana"));
        repo.salvar(aluno(2, "Bia"));
        assertEquals(5, repo.listarTodos().get(0).getId());
        assertEquals(2, repo.listarTodos().get(1).getId());
    }

    @Test
    void buscarPorIdExistenteDevolveOItem() throws Exception {
        Repositorio<Aluno> repo = new RepositorioEmMemoria<>();
        repo.salvar(aluno(1, "Ana"));
        repo.salvar(aluno(2, "Bia"));
        assertEquals("Bia", repo.buscarPorId(2).getNome());
    }

    @Test
    void buscarPorIdInexistenteLancaEntidadeNaoEncontrada() {
        Repositorio<Aluno> repo = new RepositorioEmMemoria<>();
        repo.salvar(aluno(1, "Ana"));
        assertThrows(EntidadeNaoEncontradaException.class, () -> repo.buscarPorId(99));
    }

    @Test
    void removerExistenteTiraOItemDaLista() throws Exception {
        Repositorio<Aluno> repo = new RepositorioEmMemoria<>();
        repo.salvar(aluno(1, "Ana"));
        repo.salvar(aluno(2, "Bia"));
        repo.remover(1);
        assertEquals(1, repo.listarTodos().size());
        assertThrows(EntidadeNaoEncontradaException.class, () -> repo.buscarPorId(1));
    }

    @Test
    void removerInexistenteLancaEntidadeNaoEncontrada() {
        Repositorio<Aluno> repo = new RepositorioEmMemoria<>();
        assertThrows(EntidadeNaoEncontradaException.class, () -> repo.remover(7));
    }

    @Test
    void salvarComMesmoIdSubstituiEmVezDeDuplicar() throws Exception {
        Repositorio<Aluno> repo = new RepositorioEmMemoria<>();
        repo.salvar(aluno(1, "Ana"));
        repo.salvar(aluno(1, "Ana Maria"));
        assertEquals(1, repo.listarTodos().size());
        assertEquals("Ana Maria", repo.buscarPorId(1).getNome());
    }

    @Test
    void alterarAListaDevolvidaNaoAlteraORepositorio() {
        Repositorio<Aluno> repo = new RepositorioEmMemoria<>();
        repo.salvar(aluno(1, "Ana"));
        repo.listarTodos().clear();
        assertEquals(1, repo.listarTodos().size());
    }

    @Test
    void funcionaComQualquerIdentificavelEPorInterface() throws Exception {
        Repositorio<Disciplina> repo = new RepositorioEmMemoria<>();
        repo.salvar(new Disciplina(1, "DCC305", "POO", 60));
        assertEquals("POO", repo.buscarPorId(1).getNome());
        assertInstanceOf(Repositorio.class, repo);
        assertInstanceOf(Identificavel.class, repo.buscarPorId(1));
    }

    @Test
    void metodosSaoSynchronized() throws NoSuchMethodException {
        Class<?> c = RepositorioEmMemoria.class;
        assertTrue(Modifier.isSynchronized(c.getMethod("salvar", Identificavel.class).getModifiers()));
        assertTrue(Modifier.isSynchronized(c.getMethod("buscarPorId", int.class).getModifiers()));
        assertTrue(Modifier.isSynchronized(c.getMethod("listarTodos").getModifiers()));
        assertTrue(Modifier.isSynchronized(c.getMethod("remover", int.class).getModifiers()));
    }

    @Test
    void salvarEmParaleloNaoPerdeItens() throws Exception {
        for (int rodada = 0; rodada < 20; rodada++) {
            Repositorio<Aluno> repo = new RepositorioEmMemoria<>();
            List<Thread> threads = new ArrayList<>();
            for (int t = 0; t < 10; t++) {
                final int base = t * 100;
                Thread th = new Thread(() -> {
                    for (int i = 1; i <= 100; i++) {
                        repo.salvar(aluno(base + i, "A" + (base + i)));
                    }
                });
                threads.add(th);
                th.start();
            }
            for (Thread th : threads) {
                th.join();
            }
            assertEquals(1000, repo.listarTodos().size());
        }
    }
}
