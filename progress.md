# Progresso do Projeto

## Última Atualização
2026-07-26 — **F40 (isolamento do banco de dados nos testes E2E de UI) implementada e `passing`**. Usuário percebeu que dados de teste ("Playwright Guest ...") estavam poluindo a tela de hóspedes ao testar manualmente — consequência de D-38 (E2E de UI rodavam contra o mesmo Postgres do docker-compose usado manualmente). Escolhida a solução recomendada: banco efêmero dedicado via Testcontainers (`@testcontainers/postgresql`), mesmo padrão já usado pelos E2E de API. `frontend/e2e/global-setup.ts` sobe o container + o backend apontando pra ele, e derruba os dois ao final da suíte (D-42). Efeito colateral: rodar contra um banco vazio expôs uma flakiness pré-existente de `mat-select`, corrigida com um helper de retry (`mat-select-helper.ts`). Verificado: `npx playwright test` rodado duas vezes seguidas (4 passed em ambas), confirmado via `psql` que o Postgres de desenvolvimento não ganhou nenhum registro novo.

2026-07-26 — **F38 (separação de camadas - ReservationService) e F39 (validação de disponibilidade de quarto) implementadas e `passing`** — adicionadas ao backlog após revisão do usuário pós-conclusão do backlog original. Usuário apontou dois problemas: (1) lógica de negócio direto nas controllers, sem camada de serviço; (2) criação de reserva não valida se o quarto está disponível para as datas pedidas. `ReservationService` (novo) concentra toda a lógica antes na `ReservationController` (D-40) — primeiro domínio do refactor de camadas, um domínio por vez (Guest/Room/RoomCategory ficam para próximos itens). `ReservationRepository#findOverlappingActiveReservations` + `ReservationService#create` rejeitam (409) reserva com datas sobrepostas a outra reserva ativa do mesmo quarto (D-41) — reserva já finalizada não conta como conflito. Verificado: `./mvnw test -Dtest=ReservationServiceTest,ReservationControllerTest` (29 testes), suíte completa `./mvnw test` (77, era 66), `./mvnw test -Dtest=ReservationE2ETest` contra Postgres real (11, era 9, +2 casos de conflito de disponibilidade), `npx playwright test` (4 specs F30-F33, rodados contra o backend reiniciado com o código refatorado), `./init.sh` completo.

2026-07-26 — **F33 (E2E de UI - Reserva/Check-in/Check-out) implementada e `passing`** — **última funcionalidade do backlog; `feature_list.json` está 100% `passing`.** `frontend/e2e/reservation-flow.e2e.spec.ts` cria hóspede+categoria com preços+quarto (pré-requisitos), cria reserva pela tela, faz check-in tratando os dois estados possíveis do aviso das 14h (depende do horário real da máquina, não dá pra mockar num E2E de UI real) e faz check-out conferindo o detalhamento completo. **Essa feature encontrou um bug real**: check-out no mesmo dia calendário do check-in causava HTTP 500 não tratado em produção (`DailyRateService`/`ParkingFeeService` exigiam ≥1 dia de calendário, sem essa checagem nunca ter sido exercitada contra o backend real fora de um `Clock` mockado). Com autorização do usuário, F09 foi marcado `broken`, corrigido (agora cobra a diária/taxa mínima de 1 dia em vez de rejeitar) e revalidado — ver `DECISIONS.md` D-39. Verificado: `./mvnw test` (H2, 66 testes), `./mvnw test -Dtest=ReservationE2ETest` (Postgres real, 9 testes), `npx playwright test` (suíte completa F30-F33, 4 passed), `./init.sh` completo.

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
**Nenhum item `active` (WIP=0). Backlog completo: todas as 40 funcionalidades de `feature_list.json` (F01-F19, F21-F40) estão `passing`.** Nenhuma está `broken` no momento.

## Próximo Passo Recomendado
Não há próximo passo obrigatório dentro do escopo atual de `feature_list.json` — está 100% `passing`. Débito técnico conhecido, registrado mas não pendente de nenhuma exigência: o refactor de camadas (D-40) só cobriu Reserva até agora — `GuestController`, `RoomController` e `RoomCategoryController` continuam sem `Service` dedicado. Se o usuário pedir para continuar esse refactor ou trouxer trabalho novo:
1. Rodar `./init.sh` primeiro para confirmar que o ambiente segue saudável.
2. Qualquer nova funcionalidade deve ser adicionada a `feature_list.json` antes de ser implementada, seguindo o mesmo fluxo (uma por vez, verificação + evidência, decisão registrada em `DECISIONS.md` quando houver ambiguidade, vault atualizado, commit imediato).
3. Antes de rodar qualquer suíte Playwright (`frontend/e2e/`), confirmar que o Docker Desktop está ativo — desde F40 (D-42), o backend usado pelos testes é efêmero (sobe/derruba sozinho via `globalSetup`, Postgres também efêmero via Testcontainers) e roda na porta 8080; **pare qualquer backend manual rodando nessa porta antes de rodar `npx playwright test`**, senão o setup falha rápido com uma mensagem clara em vez de travar.
4. **Se o backend estiver rodando manualmente (`spring-boot:run`) para o usuário testar pela UI**, lembrar de reiniciá-lo depois de qualquer mudança de código Java — `spring-boot:run` não recarrega classes sozinho. Reiniciar exige matar o processo `java.exe` na porta 8080 diretamente (não só o wrapper do Maven), senão o restart reaproveita o processo antigo.

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
- [x] **F33 — Testes E2E de UI - Reserva/Check-in/Check-out**: `reservation-flow.e2e.spec.ts`. Grupo F30-F33 completo — **backlog original 100% `passing`**. Encontrou e corrigiu bug real em F09 (D-39).
- [x] **F38 — Separação de camadas - ReservationService**: `ReservationService` extraído de `ReservationController` (D-40).
- [x] **F39 — Validação de disponibilidade de quarto na criação de reserva**: rejeita reserva com datas sobrepostas a outra reserva ativa do mesmo quarto (D-41).
- [x] **F40 — Isolamento do banco de dados nos testes E2E de UI**: Postgres efêmero via Testcontainers (`@testcontainers/postgresql`), `global-setup.ts` (D-42).

Detalhes de cada feature (arquivos tocados, decisões, evidência) estão em `feature_list.json` (campo `evidence`) e nos commits correspondentes — não duplicados aqui para evitar desatualização.

## Em Andamento
- Nenhum item ativo. Backlog completo.

## Bloqueado / Pendente de Confirmação
- Nenhum.
