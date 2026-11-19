package br.ufrr.dcc.projeto.modelo;

/**
 * Dados comuns a qualquer pessoa do sistema (aluno ou professor).
 * Classe abstrata: nao existe "uma pessoa generica", so Aluno e Professor.
 */
public abstract class Pessoa implements Identificavel {

    private final int id;
    private final String nome;
    private final String cpf;

    public Pessoa(int id, String nome, String cpf) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    /** Descricao do papel da pessoa; cada subclasse define o seu. */
    public abstract String getPerfil();
}
