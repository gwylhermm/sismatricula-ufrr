# SisMatricula UFRR

Sistema desktop (Java Swing) de matricula e gestao academica simplificada.
Trabalho Final da disciplina **DCC305 - Programacao Orientada a Objetos** (UFRR).

## Problema
O controle manual de turmas, vagas e matriculas gera inconsistencias: matricula acima do
limite de vagas, aluno matriculado duas vezes e dados invalidos. O sistema garante a
consistencia das vagas **mesmo com matriculas simultaneas (Threads)**.

## Funcionalidades
- Cadastrar, listar e excluir Alunos, Professores, Disciplinas e Turmas
- Matricular e cancelar matricula de um aluno em uma turma
- Listar alunos de uma turma e turmas de um aluno
- Validacao de dados com excecoes personalizadas
- Simulacao de matriculas simultaneas com Threads

## Tecnologias
Java 17+, Maven, Java Swing, NetBeans. Dados em memoria (sem banco).

## Arquitetura (pacotes `br.ufrr.dcc.projeto`)
| Pacote | Responsabilidade |
|---|---|
| `modelo` | Entidades e regras de dominio (Pessoa, Aluno, Professor, Disciplina, Turma, Matricula) |
| `excecoes` | Hierarquia de excecoes de negocio (`SisMatriculaException` e filhas) |
| `repositorio` | Armazenamento em memoria (`Repositorio<T>`, `RepositorioEmMemoria<T>`) |
| `servico` | Regras de negocio e concorrencia (`CadastroService`, `MatriculaService`, `SimuladorMatriculas`) |
| `visao` | Interface Swing (`JanelaPrincipal`) |
| `app` | Ponto de entrada (`Main`) |

Dependencias: `excecoes > modelo > repositorio > servico > visao > app`.
O diagrama de classes esta em [`docs/diagrama.puml`](docs/diagrama.puml). **O UML e o contrato**:
ninguem muda nome de classe, atributo ou assinatura sem avisar o grupo e atualizar o diagrama.

## Como executar
1. Clonar: `git clone <url-do-repositorio>`
2. NetBeans: **File > Open Project** e escolher a pasta (icone de Maven).
3. Executar a classe `br.ufrr.dcc.projeto.app.Main` (Run Project).
4. Pelo terminal: `mvn compile` e `mvn test`.

## Equipe
| Pessoa | Nome | Pacotes |
|---|---|---|
| A | (preencher) | excecoes, modelo, repositorio |
| B | (preencher) | servico |
| C | (preencher) | visao, app, integracao |

## Fluxo de trabalho (Git)
- Antes de comecar: `git pull --rebase`
- Cada um mexe so nos seus pacotes; commits pequenos citando a issue: `Turma: vagas (#8)`
- Para fechar a issue pelo commit: `Closes #8` na mensagem
- Antes de fechar uma issue, marcar **todos** os criterios de aceite dela
- Prioridades: `prioridade: alta` primeiro, depois `media`, depois `baixa`
