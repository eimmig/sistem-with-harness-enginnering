# Gestão de Hóspedes — Desafio Full-Stack

Sistema de gestão de hóspedes para um hotel: cadastro de hóspedes, reservas, check-in, check-out, cálculo de diárias (dia útil / fim de semana) e taxa de estacionamento.

> Especificação original do desafio: [`Desafio Full-Stack - TA 11.pdf`](./Desafio%20Full-Stack%20-%20TA%2011.pdf).
> Este projeto está em fase inicial (harness/infraestrutura montada, funcionalidades de negócio ainda não implementadas) — ver [`progress.md`](./progress.md) para o estado atual e [`feature_list.json`](./feature_list.json) para o backlog detalhado.

## Stack

| Camada | Tecnologia |
|---|---|
| Backend | Java 17, Spring Boot 4.1.x, Spring Data JPA, PostgreSQL, Bean Validation, Maven |
| Testes backend | JUnit 5 + Mockito (`spring-boot-starter-test`), H2 em memória para rodar sem depender do Docker |
| Frontend | Angular 19, Angular Material, RxJS |
| Testes frontend | Karma + Jasmine (padrão do Angular CLI) |
| Banco de dados | PostgreSQL 16 via Docker Compose |

## Pré-requisitos

- JDK 17 ou superior
- Node.js 18+ e npm
- Docker e Docker Compose
- Google Chrome instalado (necessário para rodar os testes do frontend em modo headless)

## Como rodar o projeto do zero

### Opção rápida: `init.sh`
```sh
./init.sh
```
Compila e testa o backend (via H2, não depende do Docker) e faz build + testa o frontend, tudo em um comando só. É o que uma sessão nova (humana ou agente) deve rodar primeiro para confirmar que o ambiente está saudável.

### Passo a passo

#### 1. Subir o banco de dados
```sh
docker compose up -d
```
Isso sobe um PostgreSQL 16 em `localhost:5433` (porta não-padrão — propositalmente diferente de 5432 para não conflitar com uma instalação local de Postgres já existente na máquina; ver [`DECISIONS.md`](./DECISIONS.md), decisão D-09), com o banco `gestao_hospedes` já criado.

#### 2. Backend
```sh
cd backend
./mvnw test              # roda os testes (usa H2 em memória, não depende do passo 1)
./mvnw spring-boot:run    # sobe a API em http://localhost:8080 (usa o Postgres do passo 1)
```
No Windows, use `mvnw.cmd` no lugar de `./mvnw` caso o shell não reconheça o script.

Credenciais do banco podem ser sobrescritas via variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD` (padrão: `postgres`/`postgres`, compatível com o `docker-compose.yml`).

#### 3. Frontend
```sh
cd frontend
npm install
npm test                  # modo watch (interativo)
npm run test:ci           # roda uma vez e sai (headless, usado em CI/verificação)
npm start                 # sobe em http://localhost:4200
```

## Estrutura do Repositório
```
projeto-senior/
├── AGENTS.md              # regras de negócio, stack e convenções do projeto
├── DECISIONS.md           # decisões de arquitetura e suposições assumidas sobre pontos ambíguos do PDF
├── progress.md            # estado atual do trabalho
├── feature_list.json      # backlog de funcionalidades, com verificação e dependências por item
├── session-handoff.md     # transferência entre sessões (bloqueios, arquivos tocados, próximo passo)
├── init.sh                # verificação completa em um comando (compila + testa os dois lados)
├── docker-compose.yml     # PostgreSQL local
├── backend/                # API Spring Boot
└── frontend/                # aplicação Angular
```

## Observações Importantes

A especificação original tem alguns pontos que não respondem sozinhos (ex.: como a diária de fim de semana — que cobre sábado e domingo como um bloco único de 14h de sábado a 12h de segunda — interage com a taxa de estacionamento e com a tela de configuração de preço por dia da semana). As suposições assumidas para seguir em frente, com a motivação de cada uma, estão documentadas em [`DECISIONS.md`](./DECISIONS.md).

## Testes

Testes unitários são obrigatórios nos dois lados, cobrindo os requisitos funcionais e as regras de negócio (ver `feature_list.json` para o mapeamento de cada funcionalidade ao(s) teste(s) correspondente(s)).
