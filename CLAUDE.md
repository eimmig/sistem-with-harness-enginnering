# CLAUDE.md

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

## Fluxo de Inicialização
Antes de escrever qualquer código, rode o entrypoint único de verificação a partir da raiz do repositório:
```sh
./init.sh
```
Isso compila e testa o backend (via H2, não depende do Docker) e faz build + testa o frontend, do zero. Só depois de `./init.sh` passar é que faz sentido rodar `docker compose up -d` (Postgres real) e subir a aplicação de verdade — comandos detalhados em [`README.md`](./README.md).

## Restrições Obrigatórias (regras de negócio)
1. Uma "diária" padrão vai do check-in às 14h de um dia ao check-out até as 12h do dia seguinte.
2. Diária de dia útil (segunda a sexta) tem valor configurável por categoria de quarto (valor sugerido de partida: R$ 120,00).
3. A diária de fim de semana é composta por três diárias individuais: sexta 14h→sábado 12h, sábado 14h→domingo 12h e domingo 14h→segunda 12h; essas três diárias devem ser somadas no fechamento.
4. O valor da diária é **configurável por categoria de quarto e por dia da semana**, através de uma tela de configuração — não é uma constante fixa no código.
5. Taxa de estacionamento (se o hóspede tiver carro e usar vaga): R$ 15,00 em diária de dia útil, R$ 20,00 em diária de fim de semana.
6. Check-in a partir das 14h00 é permitido diretamente; antes disso, o sistema deve exibir um aviso e pedir confirmação explícita do atendente antes de prosseguir. Em qualquer horário, o check-in só pode ser realizado se o quarto estiver com status `DISPONIVEL` (ver D-01/D-12 em `DECISIONS.md`).
7. Check-out até as 12h00 sem custo adicional; após esse horário, cobra-se 50% do valor da diária vigente (respeitando dia útil/fim de semana).
8. No checkout, exibir o detalhamento completo do valor total antes de confirmar (diárias + estacionamento + eventual taxa de atraso).
9. Buscar hóspedes por nome, documento e telefone.
10. Listar hóspedes atualmente hospedados (com check-in realizado, sem check-out).
11. Listar hóspedes com reserva mas sem check-in realizado.
12. Cadastro de hóspede: nome, documento, telefone (mínimo).
13. Testes unitários obrigatórios nos dois lados (JUnit no backend; framework de testes do Angular CLI no frontend) cobrindo requisitos funcionais e regras de negócio.

## Escopo
- Trabalhe em **uma funcionalidade ativa por vez** (`status: "active"` em `feature_list.json`, WIP=1). Termine e verifique antes de começar a próxima.
- Não aproveite a implementação de uma funcionalidade para "já que estou aqui" mexer em outra fora do escopo dela.
- Reserva referencia um `Quarto` específico (não apenas uma `CategoriaQuarto`); cada quarto tem número, categoria e status (`DISPONIVEL`, `SUJO`, `OCUPADO`) — ver D-05 e D-12 em `DECISIONS.md`.
- Dependências entre funcionalidades estão no campo `dependencies` de cada item em `feature_list.json` — não inicie um item cujas dependências ainda não estejam `passing`.

## Definição de Pronto
Uma funcionalidade só é considerada concluída quando passa no comando de verificação listado no campo `verification` do item correspondente em `feature_list.json`, e a evidência (comando + resultado) é registrada no campo `evidence` — nunca quando "o código foi escrito e parece certo".

## Convenções
- Código-fonte (classes, variáveis, constantes, mensagens de erro/validação) em inglês — ver D-13 em `DECISIONS.md`. A documentação do projeto (este arquivo, `DECISIONS.md`, `feature_list.json`) continua em português; é só o código que muda.

## Checklist de Encerramento de Sessão
Antes de encerrar uma sessão, garanta um estado limpo e reiniciável:
- [ ] `./init.sh` passando (build + testes de backend e frontend)
- [ ] `feature_list.json` e `progress.md` atualizados com o estado real
- [ ] `session-handoff.md` atualizado (bloqueios, arquivos tocados, próximo passo)
- [ ] Nenhum código de debug (`console.log`, `System.out.println`, `TODO` esquecido) restando
- [ ] Commit feito — a próxima sessão deve conseguir começar limpa, sem precisar perguntar nada
