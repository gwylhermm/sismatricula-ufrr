package br.ufrr.dcc.projeto.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class DisciplinaTest {

    private Disciplina criar() {
        return new Disciplina(3, "DCC305", "Programacao Orientada a Objetos", 60);
    }

    @Test
    void gettersDevolvemOsDadosDoConstrutor() {
        Disciplina d = criar();
        assertEquals(3, d.getId());
        assertEquals("DCC305", d.getCodigo());
        assertEquals("Programacao Orientada a Objetos", d.getNome());
        assertEquals(60, d.getCargaHoraria());
    }

    @Test
    void disciplinaImplementaIdentificavel() {
        assertInstanceOf(Identificavel.class, criar());
    }

    @Test
    void atributosSaoPrivados() throws NoSuchFieldException {
        for (String campo : new String[] {"id", "codigo", "nome", "cargaHoraria"}) {
            int mod = Disciplina.class.getDeclaredField(campo).getModifiers();
            assertTrue(Modifier.isPrivate(mod), campo + " deveria ser private");
        }
    }
}
