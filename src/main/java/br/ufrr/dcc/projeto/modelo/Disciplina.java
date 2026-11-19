package br.ufrr.dcc.projeto.modelo;

/**
 * Disciplina oferecida pela universidade (ex.: POO, codigo DCC305).
 */
public class Disciplina implements Identificavel {

    private final int id;
    private final String codigo;
    private final String nome;
    private final int cargaHoraria;

    public Disciplina(int id, String codigo, String nome, int cargaHoraria) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }
}
