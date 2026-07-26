# Progresso do Projeto

## Última Atualização
2026-07-25 — **F37 (refatoração visual - Reserva/Check-in/Check-out) implementada e `passing`** — **grupo F34-F37 (redesign visual) completo**. `ReservationFormComponent` trocou os campos `datetime-local` por `MatDatepicker` + campo de hora separado (padrão 14h/12h, ajustável), fundidos de volta no mesmo formato de string que o backend espera (`combineDateAndTime()`, D-37) — payload HTTP inalterado. `CheckInComponent`/`CheckOutComponent` só reaproveitaram o layout (sem campos de data próprios). Confirmado com smoke test manual completo (ng serve + backend real): calendário abre corretamente, campo de hora editável.

F34-F36 (redesign - Hóspedes/Categoria de Quarto/Quarto) e o grupo F26-F29 (E2E de API) foram concluídos nos passos anteriores.

Backlog de **F26-F37** adicionado na sessão anterior (`status: not_started`), em três grupos:
- **F26-F29**: E2E de API por domínio (Testcontainers Postgres + TestRestTemplate, sem MockMvc/H2). Decisão em `DECISIONS.md` D-29.
- **F34-F37**: refatoração visual do frontend por domínio (layout conciso, máscara de CPF/telefone/moeda via ngx-mask, `MatDatepicker` no lugar de `datetime-local`). Decisão em `DECISIONS.md` D-30.
- **F30-F33**: E2E de UI por domínio (Playwright). Passaram a depender também da refatoração visual do mesmo domínio (F34→F30, F35→F31, F36→F32, F37→F33), porque devem ser escritos contra a tela final, não contra a tela atual.

As 24 funcionalidades originais (F01–F19, F21–F25) continuam `passing`; nenhuma delas foi tocada.

Além disso, `feature_list.json` ganhou um novo status possível, `broken` (ver `DECISIONS.md` D-31): se ao rodar F26-F33 algum teste E2E provar que uma funcionalidade hoje `passing` na verdade não funciona (regressão só visível contra Postgres/HTTP/navegador reais), ela deve ser marcada `broken` em vez de continuar `passing` silenciosamente — fica registrada para conserto numa iteração futura. Nenhuma funcionalidade está `broken` no momento; isso só vai acontecer se/quando os E2E encontrarem algo.

## Objetivo Atual
Nenhum item `active` no momento (WIP=0). Grupo F26-F29 (E2E de API) completo; grupo F34-F37 (redesign visual) completo. Só falta F30-F33 (E2E de UI).

## Próximo Passo Recomendado
1. Próxima da fila: qualquer uma de **F30-F33** (E2E de UI, Playwright) — todas as suas dependências de redesign visual (F34-F37) já estão `passing`, então podem ser feitas em qualquer ordem. Sugestão: seguir a mesma ordem de domínio (F30 Hóspedes → F31 Categoria de Quarto → F32 Quarto → F33 Reserva/Check-in/Check-out).
2. A primeira feature desse grupo a rodar precisa instalar e configurar `@playwright/test` no frontend (ainda não está no `package.json`) — isso faz parte do escopo dela, não é infra prévia. Convenção de nomes (D-29): pasta `frontend/e2e/`, specs `<domínio>-flow.e2e.spec.ts`.
3. Os smoke tests de F34-F37 usaram um script Playwright ad-hoc fora do projeto (scratchpad), só para verificação manual durante a implementação — não é a mesma coisa que a suíte `@playwright/test` versionada que F30-F33 precisam criar (testes de verdade, no repositório, rodando via `npx playwright test`).
4. Rodar `./init.sh` antes de começar, para confirmar que o ambiente segue saudável (não exige Docker nem Playwright — só a verificação pontual de cada E2E de UI vai exigir o navegador headless do Playwright instalado).

## Concluído
- [x] Repositório Git inicializado (branch `main`)
- [x] `CLAUDE.md`, `DECISIONS.md`, `progress.md`, `feature_list.json`, `session-handoff.md`, `init.sh` criados
- [x] Esqueleto do backend (Spring Boot 4.1 + Java 17 + PostgreSQL/H2) — `./mvnw test` passando
- [x] Esqueleto do frontend (Angular 19 + Angular Material) — `npm run test:ci` passando
- [x] `docker-compose.yml` para o PostgreSQL local — validado end-to-end (backend conecta e sobe)
- [x] `README.md` com instruções de setup testadas manualmente
- [x] Decisões de negócio D-01 a D-28 registradas em `DECISIONS.md`; `feature_list.json` atualizado
- [x] Swagger/OpenAPI e base de conhecimento Obsidian (`docs/vault/`) criados e mantidos atualizados a cada feature
- [x] **F01 — Cadastro de hóspede**: `POST /api/guests`.
- [x] **F02 — Busca de hóspede**: `GET /api/guests?name=&document=&phone=`.
- [x] **F03 — Cadastro de categoria de quarto**: `POST /api/room-categories`.
- [x] **F04 — Configuração de preço por dia da semana**: `PUT /api/room-categories/{id}/prices`.
- [x] **F24 — Cadastro de quarto**: `POST /api/rooms`, `PATCH /api/rooms/{id}/status`.
- [x] **F05 — Criação de reserva**: `POST /api/reservations`.
- [x] **F06 — Cálculo de diária**: `DailyRateService` (serviço puro).
- [x] **F07 — Cálculo de taxa de estacionamento**: `ParkingFeeService` (serviço puro) + `Reservation.parkingRequested`.
- [x] **F08 — Check-in**: `POST /api/reservations/{id}/check-in`.
- [x] **F09 — Check-out**: `POST /api/reservations/{id}/check-out`.
- [x] **F10 — Listagem de hóspedes no hotel**: `GET /api/guests/in-hotel`.
- [x] **F11 — Listagem de hóspedes sem check-in**: `GET /api/guests/without-check-in`.
- [x] **F13 — Tela de cadastro/busca de hóspedes**: `GuestFormComponent`, `GuestSearchComponent`, `GuestsPageComponent` (rota `/guests`).
- [x] **F14 — Tela de configuração de preços**: `RoomCategoryFormComponent`, `RoomCategoryPriceComponent`, `RoomCategoriesPageComponent` (rota `/room-categories`); backend ganhou `GET /api/room-categories`.
- [x] **F25 — Tela de gestão de quartos**: `RoomFormComponent`, `RoomListComponent`, `RoomsPageComponent` (rota `/rooms`); backend ganhou `GET /api/rooms`.
- [x] **F15 — Tela de criação de reserva**: `ReservationFormComponent`, `ReservationsPageComponent` (rota `/reservations`).
- [x] **F16 — Tela de check-in**: `CheckInComponent` (rota `/check-in`); backend ganhou `GET /api/reservations/pending-check-in`.
- [x] **F17 — Tela de check-out**: `CheckOutComponent` (rota `/check-out`); backend ganhou `GET /api/reservations/pending-check-out`.
- [x] **F18 — Lista de hóspedes no hotel**: `GuestsInHotelComponent` (rota `/guests-in-hotel`).
- [x] **F19 — Lista de hóspedes sem check-in**: `GuestsWithoutCheckinComponent` (rota `/guests-without-check-in`).
- [x] **F23 — Repositório Git público**: `https://github.com/eimmig/sistem-with-harness-enginnering`, sincronizado até o commit de F19.
- [x] **F26 — Testes E2E de API - Hóspedes**: `GuestE2ETest` (Testcontainers Postgres + `TestRestTemplate`, pacote `e2e`).
- [x] **F27 — Testes E2E de API - Categoria de Quarto**: `RoomCategoryE2ETest`.
- [x] **F28 — Testes E2E de API - Quarto**: `RoomE2ETest`.
- [x] **F29 — Testes E2E de API - Reserva/Check-in/Check-out**: `ReservationE2ETest`. Grupo F26-F29 completo.
- [x] **F34 — Refatoração visual - Hóspedes**: layout compartilhado (`styles.scss`) + `ngx-mask` + `mat-card` nas telas de hóspede.
- [x] **F35 — Refatoração visual - Categoria de Quarto**: máscara de moeda nos 7 campos de preço + `mat-card`.
- [x] **F36 — Refatoração visual - Quarto**: chip colorido de status + `mat-card`.
- [x] **F37 — Refatoração visual - Reserva/Check-in/Check-out**: `MatDatepicker` + campo de hora. Grupo F34-F37 completo.

Detalhes de cada feature (arquivos tocados, decisões, evidência) estão em `feature_list.json` (campo `evidence`) e nos commits correspondentes — não duplicados aqui para evitar desatualização.

## Em Andamento
- (nenhum item ativo no momento — F37 passou para `passing`; próxima é qualquer uma de F30-F33)

## Bloqueado / Pendente de Confirmação
- Nenhum.
