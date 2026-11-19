package br.ufrr.dcc.projeto.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AlunoTest {

    private Aluno criar() {
        return new Aluno(1, "Maria Lima", "12345678901", "2024001", "Ciencia da Computacao");
    }

    @Test
    void gettersDeAlunoFuncionam() {
        Aluno a = criar();
        assertEquals("2024001", a.getMatriculaAcademica());
        assertEquals("Ciencia da Computacao", a.getCurso());
    }

    @Test
    void dadosHerdadosDePessoaFuncionam() {
        Aluno a = criar();
        assertEquals(1, a.getId());
        assertEquals("Maria Lima", a.getNome());
        assertEquals("12345678901", a.getCpf());
    }

    @Test
    void alunoEUmaPessoaEUmIdentificavel() {
        assertInstanceOf(Pessoa.class, criar());
        assertInstanceOf(Identificavel.class, criar());
    }

    @Test
    void perfilDoAlunoEAluno() {
        assertEquals("Aluno", criar().getPerfil());
    }

    @Test
    void polimorfismoViaVariavelDoTipoPessoa() {
        Pessoa p = criar();
        assertEquals("Aluno", p.getPerfil());
    }

    @Test
    void idNomeECpfNaoSaoRepetidosNaSubclasse() {
        assertThrows(NoSuchFieldException.class, () -> Aluno.class.getDeclaredField("id"));
        assertThrows(NoSuchFieldException.class, () -> Aluno.class.getDeclaredField("nome"));
        assertThrows(NoSuchFieldException.class, () -> Aluno.class.getDeclaredField("cpf"));
    }
}
