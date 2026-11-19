package br.ufrr.dcc.projeto.servico;

import br.ufrr.dcc.projeto.excecoes.DadoInvalidoException;
import br.ufrr.dcc.projeto.excecoes.EntidadeNaoEncontradaException;
import br.ufrr.dcc.projeto.modelo.Aluno;
import br.ufrr.dcc.projeto.modelo.Disciplina;
import br.ufrr.dcc.projeto.modelo.Professor;
import br.ufrr.dcc.projeto.modelo.Turma;
import br.ufrr.dcc.projeto.repositorio.RepositorioEmMemoria;

import java.util.List;

public class TesteCadastroService {

    private interface Acao {
        void executar() throws Exception;
    }

    private static final String CPF_VALIDO = "529.982.247-25";

    private static int total = 0;
    private static int falhas = 0;

    public static void main(String[] args) throws Exception {
        testarCadastrosValidos();
        testarNomeInvalido();
        testarCpfInvalido();
        testarOutrosCamposInvalidos();
        testarIds();
        testarListagemComCopiaDefensiva();
        testarExclusao();

        System.out.println();
        System.out.println("Total: " + total + " verificações, " + falhas + " falha(s).");
        if (falhas > 0) {
            System.exit(1);
        }
    }

    // ---------- cenários ----------

    private static void testarCadastrosValidos() throws Exception {
        secao("Cadastros válidos");
        CadastroService cadastro = novoCadastro();

        Aluno aluno = cadastro.cadastrarAluno("  Ana Souza  ", CPF_VALIDO, "2024001", "Ciência da Computação");
        verificar("aluno: nome sem espaços nas pontas", "Ana Souza".equals(aluno.getNome()));
        verificar("aluno: CPF guardado só com dígitos", "52998224725".equals(aluno.getCpf()));

        Professor professor = cadastro.cadastrarProfessor("Jean", "11144477735", "1234567");
        verificar("professor: CPF sem pontuação é aceito", "11144477735".equals(professor.getCpf()));

        Disciplina disciplina = cadastro.cadastrarDisciplina("DCC305", "Programação Orientada a Objetos", 60);
        verificar("disciplina cadastrada", disciplina != null);

        Turma turma = cadastro.cadastrarTurma("T01", 40, disciplina, professor);
        verificar("turma cadastrada", turma != null);

        verificar("outro CPF válido (123.456.789-09)",
                cadastro.cadastrarAluno("Caio", "123.456.789-09", "2024003", "BCC") != null);
    }

    private static void testarNomeInvalido() {
        secao("Nome inválido");
        CadastroService cadastro = novoCadastro();

        esperarDadoInvalido("nome vazio", () -> cadastro.cadastrarAluno("", CPF_VALIDO, "1", "BCC"));
        esperarDadoInvalido("nome só com espaços", () -> cadastro.cadastrarAluno("   ", CPF_VALIDO, "1", "BCC"));
        esperarDadoInvalido("nome null", () -> cadastro.cadastrarAluno(null, CPF_VALIDO, "1", "BCC"));
        esperarDadoInvalido("professor com nome vazio", () -> cadastro.cadastrarProfessor("", CPF_VALIDO, "1"));
    }

    private static void testarCpfInvalido() {
        secao("CPF inválido");
        CadastroService cadastro = novoCadastro();

        esperarDadoInvalido("CPF null", () -> cadastro.cadastrarAluno("Ana", null, "1", "BCC"));
        esperarDadoInvalido("CPF curto", () -> cadastro.cadastrarAluno("Ana", "123", "1", "BCC"));
        esperarDadoInvalido("CPF com todos os dígitos iguais", () -> cadastro.cadastrarAluno("Ana", "111.111.111-11", "1", "BCC"));
        esperarDadoInvalido("CPF com 1º dígito verificador errado", () -> cadastro.cadastrarAluno("Ana", "529.982.247-35", "1", "BCC"));
        esperarDadoInvalido("CPF com 2º dígito verificador errado", () -> cadastro.cadastrarAluno("Ana", "529.982.247-24", "1", "BCC"));
        esperarDadoInvalido("CPF com letra", () -> cadastro.cadastrarAluno("Ana", "529.982.247-2a", "1", "BCC"));
        esperarDadoInvalido("CPF com sobra no final", () -> cadastro.cadastrarAluno("Ana", "529.982.247-25abc", "1", "BCC"));
        esperarDadoInvalido("CPF com dígitos demais", () -> cadastro.cadastrarAluno("Ana", "5299822472500", "1", "BCC"));
        esperarDadoInvalido("professor com CPF inválido", () -> cadastro.cadastrarProfessor("Jean", "000.000.000-00", "1"));
    }

    private static void testarOutrosCamposInvalidos() throws Exception {
        secao("Demais campos inválidos");
        CadastroService cadastro = novoCadastro();
        Professor professor = cadastro.cadastrarProfessor("Jean", CPF_VALIDO, "1234567");
        Disciplina disciplina = cadastro.cadastrarDisciplina("DCC305", "POO", 60);

        esperarDadoInvalido("matrícula acadêmica vazia", () -> cadastro.cadastrarAluno("Ana", CPF_VALIDO, " ", "BCC"));
        esperarDadoInvalido("curso vazio", () -> cadastro.cadastrarAluno("Ana", CPF_VALIDO, "1", ""));
        esperarDadoInvalido("SIAPE vazio", () -> cadastro.cadastrarProfessor("Jean", CPF_VALIDO, ""));
        esperarDadoInvalido("disciplina com código vazio", () -> cadastro.cadastrarDisciplina("", "POO", 60));
        esperarDadoInvalido("disciplina com nome vazio", () -> cadastro.cadastrarDisciplina("D1", "", 60));
        esperarDadoInvalido("carga horária zero", () -> cadastro.cadastrarDisciplina("D1", "POO", 0));
        esperarDadoInvalido("carga horária negativa", () -> cadastro.cadastrarDisciplina("D1", "POO", -10));
        esperarDadoInvalido("turma com código vazio", () -> cadastro.cadastrarTurma("", 5, disciplina, professor));
        esperarDadoInvalido("turma com 0 vagas", () -> cadastro.cadastrarTurma("T1", 0, disciplina, professor));
        esperarDadoInvalido("turma com vagas negativas", () -> cadastro.cadastrarTurma("T1", -3, disciplina, professor));
        esperarDadoInvalido("turma sem disciplina", () -> cadastro.cadastrarTurma("T1", 5, null, professor));
        esperarDadoInvalido("turma sem professor", () -> cadastro.cadastrarTurma("T1", 5, disciplina, null));

        verificar("nada inválido foi salvo (1 professor, 1 disciplina, 0 alunos, 0 turmas)",
                cadastro.listarProfessores().size() == 1
                        && cadastro.listarDisciplinas().size() == 1
                        && cadastro.listarAlunos().isEmpty()
                        && cadastro.listarTurmas().isEmpty());
    }

    private static void testarIds() throws Exception {
        secao("Geração de ids");
        CadastroService cadastro = novoCadastro();

        Aluno primeiro = cadastro.cadastrarAluno("Ana", CPF_VALIDO, "1", "BCC");
        verificar("primeiro id é 1", primeiro.getId() == 1);

        esperarDadoInvalido("cadastro inválido no meio", () -> cadastro.cadastrarAluno("", CPF_VALIDO, "2", "BCC"));

        Aluno segundo = cadastro.cadastrarAluno("Bia", CPF_VALIDO, "3", "BCC");
        verificar("cadastro inválido não consome id (próximo é 2)", segundo.getId() == 2);

        Professor professor = cadastro.cadastrarProfessor("Jean", CPF_VALIDO, "1");
        verificar("contador é compartilhado entre os tipos (próximo é 3)", professor.getId() == 3);
    }

    private static void testarListagemComCopiaDefensiva() throws Exception {
        secao("Listagem com cópia defensiva");
        CadastroService cadastro = novoCadastro();
        cadastro.cadastrarAluno("Ana", CPF_VALIDO, "1", "BCC");
        cadastro.cadastrarAluno("Bia", CPF_VALIDO, "2", "BCC");

        List<Aluno> copia = cadastro.listarAlunos();
        copia.clear();
        verificar("limpar a lista devolvida não afeta o cadastro", cadastro.listarAlunos().size() == 2);
    }

    private static void testarExclusao() throws Exception {
        secao("Exclusão");
        CadastroService cadastro = novoCadastro();
        Aluno aluno = cadastro.cadastrarAluno("Ana", CPF_VALIDO, "1", "BCC");
        cadastro.cadastrarAluno("Bia", CPF_VALIDO, "2", "BCC");

        cadastro.excluirAluno(aluno.getId());
        verificar("exclusão de id existente remove o aluno", cadastro.listarAlunos().size() == 1);

        total++;
        try {
            cadastro.excluirAluno(999);
            falhas++;
            System.out.println("  [FALHA] excluir id inexistente (nenhuma exceção foi lançada)");
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("  [OK]    excluir id inexistente lança EntidadeNaoEncontradaException");
        }
    }

    // ---------- apoio ----------

    private static CadastroService novoCadastro() {
        return new CadastroService(
                new RepositorioEmMemoria<>(),
                new RepositorioEmMemoria<>(),
                new RepositorioEmMemoria<>(),
                new RepositorioEmMemoria<>());
    }

    private static void secao(String titulo) {
        System.out.println();
        System.out.println(titulo);
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

    private static void esperarDadoInvalido(String descricao, Acao acao) {
        total++;
        try {
            acao.executar();
            falhas++;
            System.out.println("  [FALHA] " + descricao + " (nenhuma exceção foi lançada)");
        } catch (DadoInvalidoException e) {
            System.out.println("  [OK]    " + descricao);
        } catch (Exception e) {
            falhas++;
            System.out.println("  [FALHA] " + descricao + " (exceção inesperada: "
                    + e.getClass().getSimpleName() + ")");
        }
    }
}