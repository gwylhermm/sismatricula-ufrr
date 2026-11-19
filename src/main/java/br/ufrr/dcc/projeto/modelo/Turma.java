package br.ufrr.dcc.projeto.modelo;

import br.ufrr.dcc.projeto.excecoes.EntidadeNaoEncontradaException;
import br.ufrr.dcc.projeto.excecoes.MatriculaDuplicadaException;
import br.ufrr.dcc.projeto.excecoes.VagasEsgotadasException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Turma de uma disciplina, com um professor e um numero limitado de vagas.
 * E a REGIAO CRITICA do sistema: varias threads podem tentar se matricular ao
 * mesmo tempo, entao todo acesso a lista de matriculas e feito com
 * 'synchronized' (uma thread por vez), para nunca passar do limite de vagas.
 */
public class Turma implements Identificavel {

    private final int id;
    private final String codigo;
    private final int totalVagas;
    private final Disciplina disciplina;
    private final Professor professor;
    // composicao: as matriculas pertencem a esta turma
    private final List<Matricula> matriculas;

    public Turma(int id, String codigo, int totalVagas, Disciplina disciplina, Professor professor) {
        this.id = id;
        this.codigo = codigo;
        this.totalVagas = totalVagas;
        this.disciplina = disciplina;
        this.professor = professor;
        this.matriculas = new ArrayList<>();
    }

    @Override
    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getTotalVagas() {
        return totalVagas;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    /** Vagas livres = total de vagas - matriculas ATIVAS. */
    public synchronized int getVagasDisponiveis() {
        return totalVagas - contarAtivas();
    }

    /** Copia defensiva: quem recebe a lista nao consegue alterar a turma por ela. */
    public synchronized List<Matricula> getMatriculas() {
        return new ArrayList<>(matriculas);
    }

    /**
     * Matricula o aluno na turma. A verificacao (tem vaga? ja esta matriculado?)
     * e a inclusao acontecem juntas, sem outra thread no meio: e isso que evita
     * a condicao de corrida.
     */
    public synchronized Matricula adicionarMatricula(Aluno aluno)
            throws MatriculaDuplicadaException, VagasEsgotadasException {
        Objects.requireNonNull(aluno, "aluno nao pode ser nulo");
        if (buscarAtiva(aluno) != null) {
            throw new MatriculaDuplicadaException("O aluno " + aluno.getNome()
                    + " ja esta matriculado na turma " + codigo + ".");
        }
        if (contarAtivas() >= totalVagas) {
            throw new VagasEsgotadasException("A turma " + codigo + " nao tem mais vagas.");
        }
        Matricula matricula = new Matricula(aluno);
        matriculas.add(matricula);
        return matricula;
    }

    /** Cancela a matricula ATIVA do aluno; o registro fica no historico. */
    public synchronized void cancelarMatricula(Aluno aluno) throws EntidadeNaoEncontradaException {
        Objects.requireNonNull(aluno, "aluno nao pode ser nulo");
        Matricula ativa = buscarAtiva(aluno);
        if (ativa == null) {
            throw new EntidadeNaoEncontradaException("O aluno " + aluno.getNome()
                    + " nao possui matricula ativa na turma " + codigo + ".");
        }
        ativa.cancelar();
    }

    private int contarAtivas() {
        int total = 0;
        for (Matricula m : matriculas) {
            if (m.isAtiva()) {
                total++;
            }
        }
        return total;
    }

    private Matricula buscarAtiva(Aluno aluno) {
        for (Matricula m : matriculas) {
            if (m.isAtiva() && m.getAluno().getId() == aluno.getId()) {
                return m;
            }
        }
        return null;
    }
}
