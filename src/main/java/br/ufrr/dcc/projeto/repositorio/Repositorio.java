package br.ufrr.dcc.projeto.repositorio;

import br.ufrr.dcc.projeto.excecoes.EntidadeNaoEncontradaException;
import br.ufrr.dcc.projeto.modelo.Identificavel;
import java.util.List;

/**
 * Contrato de armazenamento de entidades. Quem usa o repositorio (os servicos)
 * conhece apenas esta interface, nao como os dados sao guardados.
 *
 * @param <T> tipo da entidade guardada; precisa ter um id (Identificavel)
 */
public interface Repositorio<T extends Identificavel> {

    /** Guarda o item; se ja existir um item com o mesmo id, ele e substituido. */
    void salvar(T item);

    T buscarPorId(int id) throws EntidadeNaoEncontradaException;

    /** Devolve uma copia da lista, na ordem em que os itens foram salvos. */
    List<T> listarTodos();

    void remover(int id) throws EntidadeNaoEncontradaException;
}
