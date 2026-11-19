package br.ufrr.dcc.projeto.servico;

import br.ufrr.dcc.projeto.excecoes.SisMatriculaException;
import br.ufrr.dcc.projeto.modelo.Aluno;
import br.ufrr.dcc.projeto.modelo.Turma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SimuladorMatriculas {

    private final MatriculaService matriculaService;

    public SimuladorMatriculas(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    public List<String> simular(Turma turma, List<Aluno> alunos) {
        int total = alunos.size();
        String[] resultados = new String[total];
        Arrays.fill(resultados, "NAO CONCLUIDA");

        CountDownLatch largada = new CountDownLatch(1);
        Thread[] threads = new Thread[total];

        for (int i = 0; i < total; i++) {
            final int indice = i;
            final Aluno aluno = alunos.get(i);
            threads[i] = new Thread(() -> {
                try {
                    largada.await();
                    resultados[indice] = tentarMatricular(aluno, turma);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads[i].start();
        }

        largada.countDown();

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return new ArrayList<>(Arrays.asList(resultados));
    }

    private String tentarMatricular(Aluno aluno, Turma turma) {
        try {
            matriculaService.matricular(aluno, turma);
            return "OK: " + aluno.getNome() + " matriculado(a)";
        } catch (SisMatriculaException e) {
            return "FALHA: " + aluno.getNome() + " - " + e.getMessage();
        }
    }
}