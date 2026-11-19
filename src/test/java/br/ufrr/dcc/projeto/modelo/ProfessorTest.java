package br.ufrr.dcc.projeto.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ProfessorTest {

    private Professor criar() {
        return new Professor(2, "Carlos Pereira", "98765432100", "1234567");
    }

    @Test
    void getterDeSiapeFunciona() {
        assertEquals("1234567", criar().getSiape());
    }

    @Test
    void dadosHerdadosDePessoaFuncionam() {
        Professor p = criar();
        assertEquals(2, p.getId());
        assertEquals("Carlos Pereira", p.getNome());
        assertEquals("98765432100", p.getCpf());
    }

    @Test
    void professorEUmaPessoaEUmIdentificavel() {
        assertInstanceOf(Pessoa.class, criar());
        assertInstanceOf(Identificavel.class, criar());
    }

    @Test
    void perfilDoProfessorEProfessor() {
        assertEquals("Professor", criar().getPerfil());
    }

    @Test
    void alunoEProfessorRespondemDiferenteAoMesmoMetodo() {
        Pessoa aluno = new Aluno(1, "Maria", "11111111111", "2024001", "BCC");
        Pessoa professor = criar();
        assertEquals("Aluno", aluno.getPerfil());
        assertEquals("Professor", professor.getPerfil());
    }

    @Test
    void idNomeECpfNaoSaoRepetidosNaSubclasse() {
        assertThrows(NoSuchFieldException.class, () -> Professor.class.getDeclaredField("id"));
        assertThrows(NoSuchFieldException.class, () -> Professor.class.getDeclaredField("nome"));
        assertThrows(NoSuchFieldException.class, () -> Professor.class.getDeclaredField("cpf"));
    }
}
