package br.ufrr.dcc.projeto.modelo;

/**
 * Aluno da universidade. E uma Pessoa (heranca) com matricula academica e curso.
 */
public class Aluno extends Pessoa {

    private final String matriculaAcademica;
    private final String curso;

    public Aluno(int id, String nome, String cpf, String matriculaAcademica, String curso) {
        super(id, nome, cpf);
        this.matriculaAcademica = matriculaAcademica;
        this.curso = curso;
    }

    public String getMatriculaAcademica() {
        return matriculaAcademica;
    }

    public String getCurso() {
        return curso;
    }

    @Override
    public String getPerfil() {
        return "Aluno";
    }
}
