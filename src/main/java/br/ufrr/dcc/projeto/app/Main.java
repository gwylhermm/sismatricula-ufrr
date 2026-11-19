
package br.ufrr.dcc.projeto.app;

import br.ufrr.dcc.projeto.modelo.Aluno;
import br.ufrr.dcc.projeto.modelo.Professor;
import br.ufrr.dcc.projeto.modelo.Disciplina;
import br.ufrr.dcc.projeto.modelo.Turma;

import br.ufrr.dcc.projeto.repositorio.Repositorio;
import br.ufrr.dcc.projeto.repositorio.RepositorioEmMemoria;

import br.ufrr.dcc.projeto.servico.CadastroService;
import br.ufrr.dcc.projeto.servico.MatriculaService;
import br.ufrr.dcc.projeto.servico.SimuladorMatriculas;

import br.ufrr.dcc.projeto.visao.JanelaPrincipal;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Repositorios
            Repositorio<Aluno> alunos =
                    new RepositorioEmMemoria<>();

            Repositorio<Professor> professores =
                    new RepositorioEmMemoria<>();

            Repositorio<Disciplina> disciplinas =
                    new RepositorioEmMemoria<>();

            Repositorio<Turma> turmas =
                    new RepositorioEmMemoria<>();

            // Servicos
            CadastroService cadastro = new CadastroService(
                    alunos, professores, disciplinas, turmas);

            MatriculaService matriculas =
                    new MatriculaService();

            SimuladorMatriculas simulador =
                    new SimuladorMatriculas(matriculas);

            // Interface
            JanelaPrincipal janela = new JanelaPrincipal(
                    cadastro, matriculas, simulador);

            janela.exibir();
        });
    }
}
