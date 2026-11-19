package br.ufrr.dcc.projeto.excecoes;

/**
 * Raiz da hierarquia de erros de negocio do SisMatricula.
 * Estende Exception (checked): quem chama e obrigado a tratar ou declarar.
 */
public class SisMatriculaException extends Exception {

    public SisMatriculaException(String mensagem) {
        super(mensagem);
    }
}
