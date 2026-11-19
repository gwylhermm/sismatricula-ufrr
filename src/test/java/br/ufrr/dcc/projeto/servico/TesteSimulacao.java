package br.ufrr.dcc.projeto.servico;

import br.ufrr.dcc.projeto.excecoes.DadoInvalidoException;
import br.ufrr.dcc.projeto.modelo.Aluno;
import br.ufrr.dcc.projeto.modelo.Disciplina;
import br.ufrr.dcc.projeto.modelo.Professor;
import br.ufrr.dcc.projeto.modelo.Turma;
import br.ufrr.dcc.projeto.repositorio.RepositorioEmMemoria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Testes de concorrência do SimuladorMatriculas.
 * Execução: botão direito no arquivo > Run File (ou Shift+F6).
 */
public class TesteSimulacao {

    private static final int RODADAS = 200;

    private static int total = 0;
    private static int falhas = 0;

    public static void main(String[] args) throws Exception {
        cenarioVagasLimitadas();
        cenarioMenosAlunosQueVagas();
        cenarioMesmoAlunoVariasVezes();
        cenarioListaVazia();
        exibirUmaRodada();

        System.out.println();
        System.out.println("Total: " + total + " verificações, " + falhas + " falha(s).");
        if (falhas > 0) {
            System.exit(1);
        }
    }

    // ---------- cenários ----------

    /**
     * Turma com 5 vagas e 20 threads: tem que dar exatamente 5, nunca mais.
     */
    private static void cenarioVagasLimitadas() throws Exception {
        System.out.println("Cenário 1: 5 vagas, 20 alunos, " + RODADAS + " rodadas");
        int rodadasComProblema = 0;

        for (int rodada = 1; rodada <= RODADAS; rodada++) {
            CadastroService cadastro = novoCadastro();
            Turma turma = criarTurma(cadastro, 5);
            List<Aluno> alunos = criarAlunos(cadastro, 20);

            List<String> resultados = new SimuladorMatriculas(new MatriculaService()).simular(turma, alunos);

            boolean correta = resultados.size() == 20
                    && contar(resultados, "OK") == 5
                    && contar(resultados, "FALHA") == 15
                    && turma.getMatriculas().size() == 5
                    && turma.getVagasDisponiveis() == 0;

            if (!correta) {
                rodadasComProblema++;
                if (rodadasComProblema == 1) {
                    System.out.println("  rodada " + rodada + " com problema: "
                            + contar(resultados, "OK") + " matriculados na lista de resultados, "
                            + turma.getMatriculas().size() + " matrículas na turma");
                }
            }
        }
        verificar("em todas as " + RODADAS + " rodadas: exatamente 5 matriculados e 15 recusados",
                rodadasComProblema == 0);
    }

    private static void cenarioMenosAlunosQueVagas() throws Exception {
        System.out.println();
        System.out.println("Cenário 2: 5 vagas, 3 alunos");
        CadastroService cadastro = novoCadastro();
        Turma turma = criarTurma(cadastro, 5);
        List<Aluno> alunos = criarAlunos(cadastro, 3);

        List<String> resultados = new SimuladorMatriculas(new MatriculaService()).simular(turma, alunos);

        verificar("os 3 alunos foram matriculados", contar(resultados, "OK") == 3);
        verificar("sobraram 2 vagas", turma.getVagasDisponiveis() == 2);
    }

    /**
     * O mesmo aluno tentando se matricular várias vezes ao mesmo tempo: só 1 pode entrar.
     */
    private static void cenarioMesmoAlunoVariasVezes() throws Exception {
        System.out.println();
        System.out.println("Cenário 3: o mesmo aluno tenta 10 vezes ao mesmo tempo, " + RODADAS + " rodadas");
        int rodadasComProblema = 0;

        for (int rodada = 1; rodada <= RODADAS; rodada++) {
            CadastroService cadastro = novoCadastro();
            Turma turma = criarTurma(cadastro, 5);
            Aluno aluno = criarAlunos(cadastro, 1).get(0);
            List<Aluno> repetidos = Collections.nCopies(10, aluno);

            List<String> resultados = new SimuladorMatriculas(new MatriculaService()).simular(turma, repetidos);

            boolean correta = contar(resultados, "OK") == 1
                    && contar(resultados, "FALHA") == 9
                    && turma.getMatriculas().size() == 1;
            if (!correta) {
                rodadasComProblema++;
            }
        }
        verificar("em todas as " + RODADAS + " rodadas: apenas 1 matrícula para o mesmo aluno",
                rodadasComProblema == 0);
    }

    private static void cenarioListaVazia() throws Exception {
        System.out.println();
        System.out.println("Cenário 4: lista de alunos vazia");
        CadastroService cadastro = novoCadastro();
        Turma turma = criarTurma(cadastro, 5);

        List<String> resultados = new SimuladorMatriculas(new MatriculaService())
                .simular(turma, new ArrayList<Aluno>());

        verificar("devolve lista vazia, sem erro", resultados.isEmpty());
    }

    /**
     * Mostra o resultado de uma rodada, útil para a demonstração na defesa.
     */
    private static void exibirUmaRodada() throws Exception {
        System.out.println();
        System.out.println("Exemplo de uma rodada (5 vagas, 20 alunos):");
        CadastroService cadastro = novoCadastro();
        Turma turma = criarTurma(cadastro, 5);
        List<Aluno> alunos = criarAlunos(cadastro, 20);

        for (String linha : new SimuladorMatriculas(new MatriculaService()).simular(turma, alunos)) {
            System.out.println("  " + linha);
        }
    }

    // ---------- apoio: montagem dos dados ----------

    private static CadastroService novoCadastro() {
        return new CadastroService(
                new RepositorioEmMemoria<Aluno>(),
                new RepositorioEmMemoria<Professor>(),
                new RepositorioEmMemoria<Disciplina>(),
                new RepositorioEmMemoria<Turma>());
    }

    private static Turma criarTurma(CadastroService cadastro, int vagas) throws DadoInvalidoException {
        Professor professor = cadastro.cadastrarProfessor("Jean", "529.982.247-25", "1234567");
        Disciplina disciplina = cadastro.cadastrarDisciplina("DCC305", "POO", 60);
        return cadastro.cadastrarTurma("T01", vagas, disciplina, professor);
    }

    private static List<Aluno> criarAlunos(CadastroService cadastro, int quantidade) throws DadoInvalidoException {
        List<Aluno> alunos = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            alunos.add(cadastro.cadastrarAluno("Aluno " + i, gerarCpf(i), "2024" + i, "BCC"));
        }
        return alunos;
    }

    /**
     * Gera um CPF válido (só dígitos) a partir de um número. Serve apenas para montar dados de teste.
     */
    private static String gerarCpf(int n) {
        String base = String.format("%09d", 100000000 + n);
        String comPrimeiroDigito = base + calcularDigito(base);
        return comPrimeiroDigito + calcularDigito(comPrimeiroDigito);
    }

    private static int calcularDigito(String digitos) {
        int peso = digitos.length() + 1;
        int soma = 0;
        for (char c : digitos.toCharArray()) {
            soma += (c - '0') * peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    // ---------- apoio: verificação ----------

    private static long contar(List<String> resultados, String prefixo) {
        return resultados.stream().filter(s -> s.startsWith(prefixo)).count();
    }

    private static void verificar(String descricao, boolean condicao) {
        total++;
        if (condicao) {
            System.out.println("  [OK]    " + descricao);
        } else {
            falhas++;
            System.out.println("  [FALHA] " + descricao);
        }
    }
}