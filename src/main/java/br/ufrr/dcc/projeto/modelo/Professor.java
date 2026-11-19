package br.ufrr.dcc.projeto.modelo;

/**
 * Professor da universidade. E uma Pessoa (heranca) com numero SIAPE.
 */
public class Professor extends Pessoa {

    private final String siape;

    public Professor(int id, String nome, String cpf, String siape) {
        super(id, nome, cpf);
        this.siape = siape;
    }

    public String getSiape() {
        return siape;
    }

    @Override
    public String getPerfil() {
        return "Professor";
    }
}
