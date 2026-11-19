package br.ufrr.dcc.projeto.servico;

import br.ufrr.dcc.projeto.excecoes.DadoInvalidoException;
import br.ufrr.dcc.projeto.excecoes.EntidadeNaoEncontradaException;
import br.ufrr.dcc.projeto.modelo.Aluno;
import br.ufrr.dcc.projeto.modelo.Disciplina;
import br.ufrr.dcc.projeto.modelo.Professor;
import br.ufrr.dcc.projeto.modelo.Turma;
import br.ufrr.dcc.projeto.repositorio.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class CadastroService {

    private final Repositorio<Aluno> alunos;
    private final Repositorio<Professor> professores;
    private final Repositorio<Disciplina> disciplinas;
    private final Repositorio<Turma> turmas;
    private int proximoId = 1;

    public CadastroService(Repositorio<Aluno> alunos,
                           Repositorio<Professor> professores,
                           Repositorio<Disciplina> disciplinas,
                           Repositorio<Turma> turmas) {
        this.alunos = alunos;
        this.professores = professores;
        this.disciplinas = disciplinas;
        this.turmas = turmas;
    }

    // ---------- cadastrar ----------

    public Aluno cadastrarAluno(String nome, String cpf, String matriculaAcademica, String curso)
            throws DadoInvalidoException {
        validarTexto(nome, "Nome");
        String cpfNormalizado = normalizarCpf(cpf);
        validarTexto(matriculaAcademica, "Matrícula acadêmica");
        validarTexto(curso, "Curso");

        Aluno aluno = new Aluno(gerarId(), nome.trim(), cpfNormalizado,
                matriculaAcademica.trim(), curso.trim());
        alunos.salvar(aluno);
        return aluno;
    }

    public Professor cadastrarProfessor(String nome, String cpf, String siape)
            throws DadoInvalidoException {
        validarTexto(nome, "Nome");
        String cpfNormalizado = normalizarCpf(cpf);
        validarTexto(siape, "SIAPE");

        Professor professor = new Professor(gerarId(), nome.trim(), cpfNormalizado, siape.trim());
        professores.salvar(professor);
        return professor;
    }

    public Disciplina cadastrarDisciplina(String codigo, String nome, int cargaHoraria)
            throws DadoInvalidoException {
        validarTexto(codigo, "Código");
        validarTexto(nome, "Nome");
        validarPositivo(cargaHoraria, "Carga horária");

        Disciplina disciplina = new Disciplina(gerarId(), codigo.trim(), nome.trim(), cargaHoraria);
        disciplinas.salvar(disciplina);
        return disciplina;
    }

    public Turma cadastrarTurma(String codigo, int totalVagas, Disciplina disciplina, Professor professor)
            throws DadoInvalidoException {
        validarTexto(codigo, "Código");
        validarPositivo(totalVagas, "Total de vagas");
        validarObrigatorio(disciplina, "Disciplina");
        validarObrigatorio(professor, "Professor");

        Turma turma = new Turma(gerarId(), codigo.trim(), totalVagas, disciplina, professor);
        turmas.salvar(turma);
        return turma;
    }

    // ---------- listar (cópia defensiva) ----------

    public List<Aluno> listarAlunos() {
        return new ArrayList<>(alunos.listarTodos());
    }

    public List<Professor> listarProfessores() {
        return new ArrayList<>(professores.listarTodos());
    }

    public List<Disciplina> listarDisciplinas() {
        return new ArrayList<>(disciplinas.listarTodos());
    }

    public List<Turma> listarTurmas() {
        return new ArrayList<>(turmas.listarTodos());
    }

    // ---------- excluir ----------

    public void excluirAluno(int id) throws EntidadeNaoEncontradaException {
        alunos.remover(id);
    }

    public void excluirProfessor(int id) throws EntidadeNaoEncontradaException {
        professores.remover(id);
    }

    public void excluirDisciplina(int id) throws EntidadeNaoEncontradaException {
        disciplinas.remover(id);
    }

    public void excluirTurma(int id) throws EntidadeNaoEncontradaException {
        turmas.remover(id);
    }

    // ---------- apoio: id ----------

    private int gerarId() {
        return proximoId++;
    }

    // ---------- apoio: validações ----------

    private void validarTexto(String valor, String campo) throws DadoInvalidoException {
        if (valor == null || valor.isBlank()) {
            throw new DadoInvalidoException(campo + " não pode ser vazio.");
        }
    }

    private void validarPositivo(int valor, String campo) throws DadoInvalidoException {
        if (valor <= 0) {
            throw new DadoInvalidoException(campo + " deve ser maior que zero.");
        }
    }

    private void validarObrigatorio(Object valor, String campo) throws DadoInvalidoException {
        if (valor == null) {
            throw new DadoInvalidoException(campo + " é obrigatório.");
        }
    }

    /**
     * Valida o CPF e devolve só os 11 dígitos (sem pontos nem traço).
     */
    private String normalizarCpf(String cpf) throws DadoInvalidoException {
        if (cpf == null) {
            throw new DadoInvalidoException("CPF não pode ser vazio.");
        }
        String texto = cpf.trim();
        if (!texto.matches("\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}")) {
            throw new DadoInvalidoException("CPF inválido: " + cpf);
        }
        String digitos = texto.replaceAll("\\D", "");
        boolean todosIguais = digitos.chars().distinct().count() == 1;
        boolean dv1Ok = calcularDigito(digitos, 9) == digitos.charAt(9) - '0';
        boolean dv2Ok = calcularDigito(digitos, 10) == digitos.charAt(10) - '0';
        if (todosIguais || !dv1Ok || !dv2Ok) {
            throw new DadoInvalidoException("CPF inválido: " + cpf);
        }
        return digitos;
    }

    /**
     * Dígito verificador a partir dos primeiros 'quantidade' dígitos (pesos de quantidade+1 até 2).
     */
    private int calcularDigito(String digitos, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (digitos.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}