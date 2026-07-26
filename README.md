# Gestão de Hóspedes — Desafio Full-Stack

Sistema de gestão de hóspedes para um hotel: cadastro de hóspedes, reservas, check-in, check-out, cálculo de diárias (dia útil / fim de semana) e taxa de estacionamento.

> Especificação original do desafio: [`Desafio Full-Stack - TA 11.pdf`](./Desafio%20Full-Stack%20-%20TA%2011.pdf).
> **Backlog completo**: as 37 funcionalidades de [`feature_list.json`](./feature_list.json) estão `passing` — ver [`progress.md`](./progress.md) para o histórico e [`DECISIONS.md`](./DECISIONS.md) para as decisões de arquitetura e suposições assumidas sobre pontos ambíguos do PDF.

## Stack

| Camada | Tecnologia |
|---|---|
| Backend | Java 17, Spring Boot 4.1.x, Spring Data JPA, PostgreSQL, Bean Validation, Maven |
| Documentação da API | springdoc-openapi (Swagger UI) |
| Testes backend | JUnit 5 + Mockito (`spring-boot-starter-test`), H2 em memória para rodar sem depender do Docker |
| Frontend | Angular 19, Angular Material, RxJS, ngx-mask |
| Testes frontend | Karma + Jasmine (unitários) + Playwright (E2E de UI, navegador real) |
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

Com a API no ar (`./mvnw spring-boot:run`), a documentação interativa (Swagger UI) fica em `http://localhost:8080/swagger-ui.html`, e o contrato OpenAPI bruto em `http://localhost:8080/v3/api-docs`.

Testes E2E de API (`*E2ETest`, pacote `e2e`) usam Testcontainers (Postgres real, efêmero) e exigem Docker rodando; são excluídos do `./mvnw test` padrão e rodam só explicitamente:
```sh
./mvnw test -Dtest=GuestE2ETest,RoomCategoryE2ETest,RoomE2ETest,ReservationE2ETest
```

#### 3. Frontend
```sh
cd frontend
npm install
npm test                  # modo watch (interativo)
npm run test:ci           # testes unitários (Karma/Jasmine), roda uma vez e sai (headless)
npm start                 # sobe em http://localhost:4200
```

#### 4. Testes E2E de UI (Playwright)

Simulam o atendente navegando de verdade (backend real + Postgres real do passo 1 + frontend real, sem mocks):
```sh
cd frontend
npx playwright install chromium   # só na primeira vez
npm run test:e2e                  # roda todos os specs em frontend/e2e/
```
`playwright.config.ts` sobe o backend (`spring-boot:run`) e o frontend (`ng serve`) automaticamente antes da suíte — só é preciso que o Postgres do passo 1 (`docker compose up -d`) já esteja rodando. Como os dados de teste persistem entre execuções (sem endpoint de reset de banco), cada spec gera valores únicos por timestamp.

## Estrutura do Repositório
```
projeto-senior/
├── CLAUDE.md              # regras de negócio, stack e convenções do projeto
├── DECISIONS.md           # decisões de arquitetura e suposições assumidas sobre pontos ambíguos do PDF
├── progress.md            # estado atual do trabalho
├── feature_list.json      # backlog de funcionalidades, com verificação e dependências por item
├── session-handoff.md     # transferência entre sessões (bloqueios, arquivos tocados, próximo passo)
├── init.sh                # verificação completa em um comando (compila + testa os dois lados)
├── docker-compose.yml     # PostgreSQL local
├── docs/vault/             # base de conhecimento em Obsidian (visão geral, domínio, mapa de funcionalidades)
├── backend/                # API Spring Boot
└── frontend/                # aplicação Angular
```

## Documentação da API (Swagger / OpenAPI)

O backend expõe documentação interativa via [springdoc-openapi](https://springdoc.org/):
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Contrato OpenAPI (JSON): `http://localhost:8080/v3/api-docs`

Configuração em [`OpenApiConfig`](./backend/src/main/java/com/projetosenior/gestaohospedes/config/OpenApiConfig.java). Não é uma funcionalidade rastreada em `feature_list.json` — é infraestrutura que já preenche sozinha conforme os controllers de cada feature forem implementados.

## Base de Conhecimento (Obsidian)

Em [`docs/vault/`](./docs/vault/) há uma vault do [Obsidian](https://obsidian.md/) com visão geral do sistema, glossário de domínio, arquitetura e mapa de funcionalidades — útil para abrir com o Obsidian e navegar pelos links entre notas. Para usar: abra a pasta `docs/vault/` como vault no Obsidian. O conteúdo espelha `CLAUDE.md`, `DECISIONS.md` e `feature_list.json`, então deve ser atualizado junto com eles conforme os módulos forem implementados.

## Observações Importantes

A especificação original tem alguns pontos que não respondem sozinhos (ex.: como a estadia de fim de semana — composta por três diárias individuais: sexta 14h→sábado 12h, sábado 14h→domingo 12h e domingo 14h→segunda 12h — interage com a taxa de estacionamento e com a tela de configuração de preço por dia da semana). As suposições assumidas para seguir em frente, com a motivação de cada uma, estão documentadas em [`DECISIONS.md`](./DECISIONS.md).

## Testes

Testes unitários são obrigatórios nos dois lados, cobrindo os requisitos funcionais e as regras de negócio (ver `feature_list.json` para o mapeamento de cada funcionalidade ao(s) teste(s) correspondente(s)).
