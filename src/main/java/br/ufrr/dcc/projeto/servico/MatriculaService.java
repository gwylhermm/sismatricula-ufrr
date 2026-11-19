package br.ufrr.dcc.projeto.servico;

import br.ufrr.dcc.projeto.excecoes.EntidadeNaoEncontradaException;
import br.ufrr.dcc.projeto.excecoes.MatriculaDuplicadaException;
import br.ufrr.dcc.projeto.excecoes.VagasEsgotadasException;
import br.ufrr.dcc.projeto.modelo.Aluno;
import br.ufrr.dcc.projeto.modelo.Matricula;
import br.ufrr.dcc.projeto.modelo.Turma;

public class MatriculaService {

    public Matricula matricular(Aluno aluno, Turma turma)
            throws VagasEsgotadasException, MatriculaDuplicadaException {
        return turma.adicionarMatricula(aluno);
    }

    public void cancelar(Aluno aluno, Turma turma)
            throws EntidadeNaoEncontradaException {
        turma.cancelarMatricula(aluno);
    }
}
