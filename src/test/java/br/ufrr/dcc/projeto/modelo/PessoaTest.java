package br.ufrr.dcc.projeto.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class PessoaTest {

    /** Subclasse minima so para testar, ja que Pessoa e abstrata. */
    private static Pessoa criar() {
        return new Pessoa(7, "Ana Souza", "12345678901") {
            @Override
            public String getPerfil() {
                return "Teste";
            }
        };
    }

    @Test
    void gettersDevolvemOsDadosDoConstrutor() {
        Pessoa p = criar();
        assertEquals(7, p.getId());
        assertEquals("Ana Souza", p.getNome());
        assertEquals("12345678901", p.getCpf());
    }

    @Test
    void pessoaImplementaIdentificavel() {
        assertInstanceOf(Identificavel.class, criar());
    }

    @Test
    void pessoaEAbstrata() {
        assertTrue(Modifier.isAbstract(Pessoa.class.getModifiers()));
    }

    @Test
    void getPerfilEAbstrato() throws NoSuchMethodException {
        assertTrue(Modifier.isAbstract(Pessoa.class.getMethod("getPerfil").getModifiers()));
    }

    @Test
    void getPerfilEPolimorfico() {
        Pessoa p = criar();
        assertEquals("Teste", p.getPerfil());
    }

    @Test
    void atributosSaoPrivados() throws NoSuchFieldException {
        for (String campo : new String[] {"id", "nome", "cpf"}) {
            int mod = Pessoa.class.getDeclaredField(campo).getModifiers();
            assertTrue(Modifier.isPrivate(mod), campo + " deveria ser private");
        }
    }
}
