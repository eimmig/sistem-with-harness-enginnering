# Progresso do Projeto

## Última Atualização
2026-07-25 — F16 (Tela de check-in) implementada e passando: backend ganhou `GET /api/reservations/pending-check-in` (D-27, expõe query já existente desde F10/F11); `CheckInComponent` lista reservas pendentes, aviso das 14h é inline na linha (sem modal — D-27); 44/44 testes Karma passando, `ng build` sem erros.

## Objetivo Atual
Backend de negócio central completo (F01–F11, F24). F13, F14, F25, F15 e F16 são as telas de frontend concluídas até agora. Restam: F17 (check-out), F18 (lista no hotel), F19 (lista sem check-in) — todas sem dependências pendentes; e F23 (repositório Git público — fora do escopo de automação, requer ação do usuário).

## Próximo Passo Recomendado
1. F17 (check-out) segue o mesmo padrão de F16: vai precisar de `GET /api/reservations/pending-check-out` (reaproveitando `findByActualCheckInIsNotNullAndActualCheckOutIsNull()`, já existente no `ReservationRepository`) — mesmo raciocínio de D-27, adicionar dentro do escopo de F17.
2. F18/F19 são as mais simples (só consomem `GET /api/guests/in-hotel` e `/without-check-in`, já existentes) — podem ser feitas a qualquer momento, sem bloqueio.
3. F23 (repositório Git público) não pode ser concluída autonomamente — publicar um repositório público e compartilhar o link é uma decisão/ação do usuário (credenciais, conta, visibilidade). Sinalizar como bloqueio quando for a única pendência restante.
4. Re-rodar `./init.sh` antes de considerar cada funcionalidade concluída.

## Concluído
- [x] Repositório Git inicializado (branch `main`)
- [x] `CLAUDE.md`, `DECISIONS.md`, `progress.md`, `feature_list.json`, `session-handoff.md`, `init.sh` criados
- [x] Esqueleto do backend (Spring Boot 4.1 + Java 17 + PostgreSQL/H2) — `./mvnw test` passando
- [x] Esqueleto do frontend (Angular 19 + Angular Material) — `npm run test:ci` passando
- [x] `docker-compose.yml` para o PostgreSQL local — validado end-to-end (backend conecta e sobe)
- [x] `README.md` com instruções de setup testadas manualmente
- [x] Decisões de negócio D-01 a D-22 registradas em `DECISIONS.md`; `feature_list.json` atualizado
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

Detalhes de cada feature (arquivos tocados, decisões, evidência) estão em `feature_list.json` (campo `evidence`) e nos commits correspondentes — não duplicados aqui para evitar desatualização.

## Em Andamento
- (nenhum item ativo no momento — F11 passou para `passing`)

## Bloqueado / Pendente de Confirmação
- F23 (repositório Git público) depende de ação do usuário fora do escopo de implementação de código.
