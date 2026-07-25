# Progresso do Projeto

## Última Atualização
2026-07-25 — F05 (Criação de reserva) implementado e passando: `Reservation` (hóspede + quarto + datas previstas de entrada/saída), `ReservationController` (`POST /api/reservations`, valida existência de hóspede/quarto e que check-out é depois do check-in), `ReservationControllerTest` + `ReservationRepositoryTest` verdes.

## Objetivo Atual
F01–F05 e F24 concluídos. Pronto para escolher a próxima funcionalidade `not_started` sem dependências pendentes (candidatas: F06 — cálculo de diária — e F07 — cálculo de taxa de estacionamento —, ambas dependem de F04/F05, já `passing`).

## Próximo Passo Recomendado
1. Escolher a próxima funcionalidade em `feature_list.json` (F06 ou F07). Ambas sinalizadas em `feature_list.json` (campo `notes`) como as de maior risco — merecem mais casos de teste, cobrindo a transição entre dias com preços diferentes (sexta/sábado/domingo/segunda). F07 depende de decisão pendente sobre onde capturar "hóspede tem carro e usa vaga" (ver D-18 em `DECISIONS.md`) — considerar adicionar esse campo à `Reservation` como parte de F07.
2. Re-rodar `./init.sh` antes de considerar cada funcionalidade concluída.

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

## Em Andamento
- (nenhum item ativo no momento — F05 passou para `passing`)

## Bloqueado / Pendente de Confirmação
- (nenhum bloqueio no momento)
