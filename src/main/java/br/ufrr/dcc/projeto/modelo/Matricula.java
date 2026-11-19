package br.ufrr.dcc.projeto.modelo;

import java.time.LocalDate;

/**
 * Matricula de um aluno em uma turma. Nasce ativa, com a data de hoje,
 * e pode ser cancelada (fica inativa, mas o registro continua existindo).
 * Quem controla as matriculas e a Turma (composicao).
 */
public class Matricula {

    private final Aluno aluno;
    private final LocalDate dataMatricula;
    // volatile: a tela pode ler 'ativa' enquanto outra thread cancela
    private volatile boolean ativa;

    public Matricula(Aluno aluno) {
        this.aluno = aluno;
        this.dataMatricula = LocalDate.now();
        this.ativa = true;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void cancelar() {
        this.ativa = false;
    }
}
