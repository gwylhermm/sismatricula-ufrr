
package br.ufrr.dcc.projeto.app;

import br.ufrr.dcc.projeto.visao.JanelaPrincipal;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JanelaPrincipal janela = new JanelaPrincipal();
            janela.exibir();
        });
    }
}

