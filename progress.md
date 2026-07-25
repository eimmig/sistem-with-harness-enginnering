# Progresso do Projeto

## Última Atualização
2026-07-25 — F08 (Check-in) implementado e passando: `Reservation.actualCheckIn` (novo campo), `Clock` injetável (`ClockConfig`) para "agora" testável, `ReservationController#checkIn` (`POST /api/reservations/{id}/check-in`) — exige confirmação explícita antes das 14h, bloqueia se o quarto não estiver `AVAILABLE` (409), marca quarto como `OCCUPIED`; `ReservationControllerTest` (+7 testes, incluindo os 3 exigidos: `checkInBefore2pm`, `checkInValid`, `checkInRoomUnavailable`) verde.

## Objetivo Atual
F01–F08 e F24 concluídos. Pronto para escolher a próxima funcionalidade `not_started` sem dependências pendentes (candidata óbvia: F09 — check-out — depende de F06, F07 e F08, todas `passing`).

## Próximo Passo Recomendado
1. Implementar F09 (check-out): calcular o total (diárias via `DailyRateService` + estacionamento via `ParkingFeeService` + eventual taxa de atraso de 50% se check-out após 12h, regra #7), exibir o detalhamento completo antes de confirmar (regra #8), e marcar o quarto como... `SUJO` provavelmente (não `DISPONIVEL` diretamente — precisa de limpeza antes do próximo hóspede; decisão a registrar). Verificação exige `checkOutLate` + `checkOutBreakdown`.
2. F09 precisa de um campo `actualCheckOut` em `Reservation` (ainda não existe) e reaproveita o `Clock` já injetado em `ReservationController` (F08) para "agora" testável.
3. Re-rodar `./init.sh` antes de considerar cada funcionalidade concluída.

## Estado Atual
- Status dos testes: `./mvnw test` (backend, via H2) e `npm run test:ci` (frontend, via Chrome headless) passando do zero — confirmado por `./init.sh` em 2026-07-25.
- Backend validado também contra o PostgreSQL real do `docker-compose.yml` (sobe sem erros na porta 8080).

## Concluído
- [x] Repositório Git inicializado (branch `main`)
- [x] `CLAUDE.md`, `DECISIONS.md`, `progress.md`, `feature_list.json`, `session-handoff.md`, `init.sh` criados
- [x] Esqueleto do backend (Spring Boot 4.1 + Java 17 + PostgreSQL/H2) — `./mvnw test` passando
- [x] Esqueleto do frontend (Angular 19 + Angular Material) — `npm run test:ci` passando
- [x] `docker-compose.yml` para o PostgreSQL local — validado end-to-end (backend conecta e sobe)
- [x] `README.md` com instruções de setup testadas manualmente
- [x] Decisões de negócio D-01, D-02, D-03, D-05 confirmadas; D-12 e D-13 registradas; `feature_list.json` atualizado (F24/F25 adicionados, identificadores em inglês)
- [x] Swagger/OpenAPI (springdoc 3.0.3) configurado no backend (`OpenApiConfig`), validado com `./mvnw test`; D-14 registrada
- [x] Base de conhecimento Obsidian criada em `docs/vault/` (visão geral, glossário, arquitetura, mapa de funcionalidades, uma nota por módulo)
- [x] **F01 — Cadastro de hóspede**: `Guest` (entidade), `GuestRepository`, `GuestRequest`/`GuestResponse` (DTOs com Bean Validation), `GuestController` (`POST /api/guests`); `GuestControllerTest` (4 testes) + `GuestRepositoryTest` (2 testes) passando; evidência registrada em `feature_list.json`.
- [x] **F02 — Busca de hóspede**: `GuestSpecifications` (filtros `nameContains`/`documentContains`/`phoneContains`), `GuestRepository` estendendo `JpaSpecificationExecutor<Guest>`, `GuestController#search` (`GET /api/guests?name=&document=&phone=`, combinação AND, sem filtros retorna todos); `GuestControllerTest` (+3 testes) + `GuestRepositoryTest` (+3 testes) passando; decisão de design em D-15; evidência registrada em `feature_list.json`.
- [x] **F03 — Cadastro de categoria de quarto**: `RoomCategory` (entidade, campos `id`/`name`), `RoomCategoryRepository`, `RoomCategoryRequest`/`RoomCategoryResponse` (DTOs com Bean Validation), `RoomCategoryController` (`POST /api/room-categories`); `RoomCategoryControllerTest` (2 testes) + `RoomCategoryRepositoryTest` (2 testes) passando; evidência registrada em `feature_list.json`. Preço por dia da semana fica para F04 (não incluído aqui, fora do escopo desta feature).
- [x] **F04 — Configuração de preço por dia da semana**: `RoomCategory.prices` (`Map<DayOfWeek, BigDecimal>`, `@ElementCollection` em tabela `room_category_price`), `RoomCategoryPricesRequest` (DTO com validação `@NotNull`/`@Positive` por valor do map), `RoomCategoryController#updatePrices` (`PUT /api/room-categories/{id}/prices` — exige as 7 diárias presentes e positivas, 404 se categoria não existir); `RoomCategoryControllerTest` (+4 testes) + `RoomCategoryRepositoryTest` (+1 teste, com flush/clear do `EntityManager` para validar round-trip real no banco) passando; decisão de design em D-16; evidência registrada em `feature_list.json`.
- [x] **F24 — Cadastro de quarto**: `Room` (entidade: `number` String, `roomCategory` ManyToOne, `status` enum), `RoomStatus` (`AVAILABLE`/`DIRTY`/`OCCUPIED` — tradução de DISPONIVEL/SUJO/OCUPADO registrada no glossário D-13), `RoomRepository`, `RoomRequest`/`RoomStatusRequest`/`RoomResponse` (DTOs), `RoomController` (`POST /api/rooms` — 404 se categoria não existir, cria com status `AVAILABLE`; `PATCH /api/rooms/{id}/status` — 404 se quarto não existir); `RoomControllerTest` (5 testes) + `RoomRepositoryTest` (2 testes) passando; decisão de design em D-17; evidência registrada em `feature_list.json`.
- [x] **F05 — Criação de reserva**: `Reservation` (entidade: `guest` ManyToOne, `room` ManyToOne, `expectedCheckIn`/`expectedCheckOut` `LocalDateTime`), `ReservationRepository`, `ReservationRequest`/`ReservationResponse` (DTOs, com summaries aninhados de hóspede/quarto), `ReservationController` (`POST /api/reservations` — 404 se hóspede ou quarto não existirem, 400 se check-out não for depois do check-in; **não** valida status do quarto na criação, só no check-in — ver D-18); `ReservationControllerTest` (5 testes) + `ReservationRepositoryTest` (1 teste) passando; decisão de design em D-18; evidência registrada em `feature_list.json`.
- [x] **F06 — Cálculo de diária**: `DailyRateService` (pacote `dailyrate`, serviço puro sem endpoint), método `calculate(RoomCategory, LocalDateTime checkIn, LocalDateTime checkOut)` — número de noites via `ChronoUnit.DAYS.between` nas datas de calendário, cada noite atribuída ao dia da semana em que começa, preço somado a partir de `RoomCategory.prices`; `DailyRateServiceTest` (5 testes, incluindo o cenário exato da regra #3: sex→sáb→dom→seg 12h = 3 diárias) passando; decisão de design em D-19; evidência registrada em `feature_list.json`.
- [x] **F07 — Cálculo de taxa de estacionamento**: `Reservation.parkingRequested` (boolean, default `false` — campo novo, decisão D-20), `ReservationRequest`/`ReservationResponse` atualizados para incluir o campo; `ParkingFeeService` (pacote `parkingfee`, serviço puro), método `calculate(boolean parkingRequested, LocalDateTime checkIn, LocalDateTime checkOut)` — R$15,00/noite em dia útil (segunda-sexta), R$20,00/noite em fim de semana (sábado-domingo), zero se `parkingRequested=false`; `ParkingFeeServiceTest` (6 testes) passando; testes de `Reservation`/`ReservationController` atualizados para o novo campo; decisão de design em D-20; evidência registrada em `feature_list.json`.
- [x] **F08 — Check-in**: `Reservation.actualCheckIn` (campo novo, `LocalDateTime` nullable), `ClockConfig` (bean `Clock.systemDefaultZone()`, injetado no `ReservationController` para "agora" testável — mock de `Clock` nos testes via `clock.instant()`/`clock.getZone()`), `CheckInRequest` (DTO, `confirmedByAttendant` opcional), `ReservationController#checkIn` (`POST /api/reservations/{id}/check-in`): 404 se reserva não existir, 409 se já tem check-in ou quarto não está `AVAILABLE`, 400 se antes das 14h sem confirmação, senão marca `actualCheckIn` e muda quarto para `OCCUPIED`; `ReservationControllerTest` (+7 testes, incluindo os 3 exigidos pela verificação) passando; decisão de design em D-21; evidência registrada em `feature_list.json`.

## Em Andamento
- (nenhum item ativo no momento — F08 passou para `passing`)

## Bloqueado / Pendente de Confirmação
- (nenhum bloqueio no momento)
