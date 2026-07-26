# Progresso do Projeto

## Última Atualização
2026-07-26 — **F32 (E2E de UI - Quarto) implementada e `passing`**. `frontend/e2e/room-flow.e2e.spec.ts` cria uma categoria de quarto, cadastra um quarto vinculado a ela pela tela, confirma que nasce com status "Disponível" (D-17) e testa as 3 transições de status (Disponível→Sujo→Ocupado→Disponível) pelo `mat-select-trigger` da lista, verificando o chip colorido (F36). Verificado com `npx playwright test room-flow` rodado duas vezes seguidas: 1 passed em ambas.

2026-07-26 — **F31 (E2E de UI - Categoria de Quarto) implementada e `passing`**. `frontend/e2e/room-category-flow.e2e.spec.ts` cobre cadastro de categoria, seleção no `mat-select`, configuração dos 7 preços por dia da semana (com vírgula decimal explícita na digitação) e confirmação de persistência ao recarregar a tela. Observações técnicas sobre interação com `mat-select`/máscara de moeda em testes automatizados registradas em addendum de D-38. Verificado com `npx playwright test room-category-flow` rodado duas vezes seguidas: 1 passed em ambas.

2026-07-26 — **F30 (E2E de UI - Hóspedes) implementada e `passing`** — primeira feature do grupo F30-F33. `@playwright/test@1.62.0` instalado no frontend; `playwright.config.ts` criado com `webServer` array subindo backend real (`mvnw.cmd spring-boot:run`) + Postgres real (docker-compose) e frontend real (`ng serve`) juntos (D-38). `frontend/e2e/guest-flow.e2e.spec.ts` cobre cadastro pela tela, busca por nome/documento/telefone e navegação para as listas de hóspedes no hotel/sem check-in. Verificado com `npx playwright test guest-flow`: 1 passed.

2026-07-25 — **F37 (refatoração visual - Reserva/Check-in/Check-out) implementada e `passing`** — **grupo F34-F37 (redesign visual) completo**. `ReservationFormComponent` trocou os campos `datetime-local` por `MatDatepicker` + campo de hora separado (padrão 14h/12h, ajustável), fundidos de volta no mesmo formato de string que o backend espera (`combineDateAndTime()`, D-37) — payload HTTP inalterado. `CheckInComponent`/`CheckOutComponent` só reaproveitaram o layout (sem campos de data próprios). Confirmado com smoke test manual completo (ng serve + backend real): calendário abre corretamente, campo de hora editável.

F34-F36 (redesign - Hóspedes/Categoria de Quarto/Quarto) e o grupo F26-F29 (E2E de API) foram concluídos nos passos anteriores.

Backlog de **F26-F37** adicionado na sessão anterior (`status: not_started`), em três grupos:
- **F26-F29**: E2E de API por domínio (Testcontainers Postgres + TestRestTemplate, sem MockMvc/H2). Decisão em `DECISIONS.md` D-29.
- **F34-F37**: refatoração visual do frontend por domínio (layout conciso, máscara de CPF/telefone/moeda via ngx-mask, `MatDatepicker` no lugar de `datetime-local`). Decisão em `DECISIONS.md` D-30.
- **F30-F33**: E2E de UI por domínio (Playwright). Passaram a depender também da refatoração visual do mesmo domínio (F34→F30, F35→F31, F36→F32, F37→F33), porque devem ser escritos contra a tela final, não contra a tela atual.

As 24 funcionalidades originais (F01–F19, F21–F25) continuam `passing`; nenhuma delas foi tocada.

Além disso, `feature_list.json` ganhou um novo status possível, `broken` (ver `DECISIONS.md` D-31): se ao rodar F26-F33 algum teste E2E provar que uma funcionalidade hoje `passing` na verdade não funciona (regressão só visível contra Postgres/HTTP/navegador reais), ela deve ser marcada `broken` em vez de continuar `passing` silenciosamente — fica registrada para conserto numa iteração futura. Nenhuma funcionalidade está `broken` no momento; isso só vai acontecer se/quando os E2E encontrarem algo.

## Objetivo Atual
F33 (E2E de UI - Reserva/Check-in/Check-out) marcado `active` — última funcionalidade do backlog. F30/F31/F32 completos; grupo F26-F29 (E2E de API) completo; grupo F34-F37 (redesign visual) completo.

## Próximo Passo Recomendado
1. Implementar **F33** (E2E de UI - Reserva/Check-in/Check-out) — infra do Playwright (`@playwright/test`, `playwright.config.ts`, D-38) já criada por F30 e reaproveitável. Com F33 `passing`, todo o `feature_list.json` estará completo.
2. Antes de rodar qualquer suíte Playwright, confirmar que o Docker Desktop está ativo e o container `gestao-hospedes-db` (docker-compose) está `healthy` — o `webServer` do backend em `playwright.config.ts` sobe via `spring-boot:run` normal (Postgres real na porta 5433), não Testcontainers. Se o Docker não estiver rodando, iniciar com `Start-Process 'C:\Program Files\Docker\Docker\Docker Desktop.exe'` e aguardar (~20-30s) antes de rodar `npx playwright test`.
3. Cada spec deve gerar dados únicos por timestamp (`Date.now()`) para evitar colisão entre execuções, já que o Postgres do docker-compose persiste dados entre runs (ver D-38).
4. Rodar `./init.sh` antes de cada feature nova, para confirmar que o ambiente segue saudável (não exige Docker nem Playwright — só a verificação pontual de cada E2E de UI vai exigir o navegador headless do Playwright instalado e o Docker rodando).

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
- [x] **F30 — Testes E2E de UI - Hóspedes**: `@playwright/test` instalado; `playwright.config.ts` (D-38); `guest-flow.e2e.spec.ts`.
- [x] **F31 — Testes E2E de UI - Categoria de Quarto**: `room-category-flow.e2e.spec.ts`.
- [x] **F32 — Testes E2E de UI - Quarto**: `room-flow.e2e.spec.ts`.

Detalhes de cada feature (arquivos tocados, decisões, evidência) estão em `feature_list.json` (campo `evidence`) e nos commits correspondentes — não duplicados aqui para evitar desatualização.

## Em Andamento
- **F33 (E2E de UI - Reserva/Check-in/Check-out)** — active.

## Bloqueado / Pendente de Confirmação
- Nenhum.
