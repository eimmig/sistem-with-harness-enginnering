# AGENTS.md

## Visão Geral
Sistema de gestão de hóspedes para um hotel: cadastro de hóspedes, reservas, check-in e check-out, com cálculo automático de diárias, taxa de estacionamento e taxa de atraso na saída.

- Especificação original: [`Desafio Full-Stack - TA 11.pdf`](./Desafio%20Full-Stack%20-%20TA%2011.pdf) (raiz do repositório).
- Lista de funcionalidades, com critério de verificação e dependências por item: [`feature_list.json`](./feature_list.json).
- Decisões de arquitetura e suposições assumidas sobre pontos ambíguos da especificação: [`DECISIONS.md`](./DECISIONS.md).
- Progresso corrente (o que está feito, em andamento, bloqueado): [`progress.md`](./progress.md).
- Transferência entre sessões (bloqueios, arquivos tocados, próximo passo): [`session-handoff.md`](./session-handoff.md).

## Stack
- **Backend**: Java 17, Spring Boot 4.1.x, Maven, Spring Data JPA, PostgreSQL (runtime) / H2 (testes), Bean Validation, JUnit 5 + Mockito.
- **Frontend**: Angular 19, Angular Material, RxJS, Karma + Jasmine (padrão do Angular CLI).
- **Banco**: PostgreSQL local via Docker Compose (`docker-compose.yml` na raiz).

## Startup Workflow
**Before writing code**, rode o entrypoint único de verificação a partir da raiz do repositório:
```sh
./init.sh
```
Isso compila e testa o backend (via H2, não depende do Docker) e faz build + testa o frontend, do zero. Só depois de `./init.sh` passar é que faz sentido rodar `docker compose up -d` (Postgres real) e subir a aplicação de verdade — comandos detalhados em [`README.md`](./README.md).

## Restrições Obrigatórias (regras de negócio)
1. Uma "diária" padrão vai do check-in às 14h de um dia ao check-out até as 12h do dia seguinte.
2. Diária de dia útil (segunda a sexta) tem valor configurável por categoria de quarto (valor sugerido de partida: R$ 120,00).
3. A diária de fim de semana é um **bloco único**: começa às 14h de sábado e termina às 12h de segunda — cobre sábado e domingo como **uma única diária**, não duas (valor sugerido de partida: R$ 180,00).
4. O valor da diária é **configurável por categoria de quarto e por dia da semana**, através de uma tela de configuração — não é uma constante fixa no código.
5. Taxa de estacionamento (se o hóspede tiver carro e usar vaga): R$ 15,00 em diária de dia útil, R$ 20,00 em diária de fim de semana.
6. Check-in só é permitido a partir das 14h00; antes disso o sistema deve emitir um alerta.
7. Check-out até as 12h00 sem custo adicional; após esse horário, cobra-se 50% do valor da diária vigente (respeitando dia útil/fim de semana).
8. No checkout, exibir o detalhamento completo do valor total antes de confirmar (diárias + estacionamento + eventual taxa de atraso).
9. Buscar hóspedes por nome, documento e telefone.
10. Listar hóspedes atualmente hospedados (com check-in realizado, sem check-out).
11. Listar hóspedes com reserva mas sem check-in realizado.
12. Cadastro de hóspede: nome, documento, telefone (mínimo).
13. Testes unitários obrigatórios nos dois lados (JUnit no backend; framework de testes do Angular CLI no frontend) cobrindo requisitos funcionais e regras de negócio.

## Suposições Assumidas (a confirmar com o solicitante do desafio)
A especificação tem pontos que não respondem sozinhos — em vez de travar, assumimos uma interpretação e registramos aqui e em `DECISIONS.md`.

- **[D-01]** Alerta de check-in antes das 14h é um aviso não bloqueante (o atendente confirma e pode prosseguir mesmo assim).
- **[D-02]** O preço aplicado à diária de fim de semana (bloco sábado→segunda) é o valor configurado para "Sábado" na grade de preços da categoria; o valor de "Domingo" fica registrado mas não é usado isoladamente para cobrança, já que domingo nunca inicia uma diária própria dentro dessa regra.
- **[D-03]** Taxa de estacionamento na diária de fim de semana é cobrada uma única vez (R$ 20,00), decorrência direta de D-02.

## Scope / Escopo
- **One feature at a time** — trabalhe em **uma funcionalidade ativa por vez** (`status: "active"` em `feature_list.json`, WIP=1). Termine e verifique antes de começar a próxima.
- Não aproveite a implementação de uma funcionalidade para "já que estou aqui" mexer em outra fora do escopo dela.
- Reserva referencia uma `CategoriaQuarto`, não um quarto físico individual — controle de inventário/numeração de quartos está fora do escopo pedido (ver D-05 em `DECISIONS.md`).
- Dependências entre funcionalidades estão no campo `dependencies` de cada item em `feature_list.json` — não inicie um item cujas dependências ainda não estejam `passing`.

## Definition of Done
Uma funcionalidade só é considerada **done only when** ela passa no comando de verificação listado no campo `verification` do item correspondente em `feature_list.json`, e a evidência (comando + resultado) é registrada no campo `evidence` — nunca quando "o código foi escrito e parece certo".

## Convenções
- Domínio modelado em português (`Hospede`, `Reserva`, `CategoriaQuarto`); nomes técnicos seguem convenção padrão Java/Angular.
- Arquivos de harness (`AGENTS.md`, `feature_list.json`, `progress.md`, `session-handoff.md`) usam títulos bilíngues de propósito — texto livre em português, âncoras estruturais em inglês para ferramentas de validação.

## End of Session Checklist
**Before ending** uma sessão, garanta um estado limpo e restartable:
- [ ] `./init.sh` passando (build + testes de backend e frontend)
- [ ] `feature_list.json` e `progress.md` atualizados com o estado real
- [ ] `session-handoff.md` atualizado (bloqueios, arquivos tocados, próximo passo)
- [ ] Nenhum código de debug (`console.log`, `System.out.println`, `TODO` esquecido) restando
- [ ] Commit feito — a próxima sessão deve conseguir começar limpa, sem precisar perguntar nada
