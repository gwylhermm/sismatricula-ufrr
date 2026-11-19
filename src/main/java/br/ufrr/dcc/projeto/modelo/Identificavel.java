package br.ufrr.dcc.projeto.modelo;

/**
 * Contrato de toda entidade que possui identificador unico.
 * O repositorio usa esse contrato para buscar e remover itens por id.
 */
public interface Identificavel {

    int getId();
}
