package br.ufrr.dcc.projeto.repositorio;

import br.ufrr.dcc.projeto.excecoes.EntidadeNaoEncontradaException;
import br.ufrr.dcc.projeto.modelo.Identificavel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Repositorio que guarda os itens em uma lista na memoria (some ao fechar o programa).
 * Os metodos sao 'synchronized' porque o simulador usa varias threads ao mesmo tempo.
 *
 * @param <T> tipo da entidade guardada
 */
public class RepositorioEmMemoria<T extends Identificavel> implements Repositorio<T> {

    private final List<T> itens;

    public RepositorioEmMemoria() {
        this.itens = new ArrayList<>();
    }

    @Override
    public synchronized void salvar(T item) {
        Objects.requireNonNull(item, "item nao pode ser nulo");
        int indice = indiceDe(item.getId());
        if (indice >= 0) {
            itens.set(indice, item);
        } else {
            itens.add(item);
        }
    }

    @Override
    public synchronized T buscarPorId(int id) throws EntidadeNaoEncontradaException {
        int indice = indiceDe(id);
        if (indice < 0) {
            throw new EntidadeNaoEncontradaException("Nenhum registro encontrado com o id " + id + ".");
        }
        return itens.get(indice);
    }

    @Override
    public synchronized List<T> listarTodos() {
        return new ArrayList<>(itens);
    }

    @Override
    public synchronized void remover(int id) throws EntidadeNaoEncontradaException {
        int indice = indiceDe(id);
        if (indice < 0) {
            throw new EntidadeNaoEncontradaException("Nenhum registro encontrado com o id " + id + ".");
        }
        itens.remove(indice);
    }

    /** Posicao do item com esse id, ou -1 se nao existir. */
    private int indiceDe(int id) {
        for (int i = 0; i < itens.size(); i++) {
            if (itens.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
}
