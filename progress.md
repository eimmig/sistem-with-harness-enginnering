# Progresso do Projeto

## Última Atualização
2026-07-25 — F19 (Lista de hóspedes sem check-in) implementada e passando: `GuestsWithoutCheckinComponent` (rota `/guests-without-check-in`), consome `GET /api/guests/without-check-in` (já existente desde F11, nenhuma mudança no backend); 51/51 testes Karma passando, `ng build` sem erros. **Com F19, todo o backlog de implementação (F01–F19, F21, F22, F24, F25) está `passing`.**

## Objetivo Atual
Todo o código está implementado e verificado. **Único item restante: F23 (repositório Git público)** — não é uma tarefa de código, é uma decisão/ação do usuário: criar/publicar o repositório num host Git (GitHub, GitLab, etc.), decidir a conta/organização, garantir visibilidade pública, e compartilhar o link. Fora do escopo do que pode ser feito autonomamente nesta sessão.

## Próximo Passo Recomendado
1. Perguntar ao usuário como deseja publicar o repositório (conta/organização, nome do repositório) e, se autorizado, executar os comandos Git necessários (`git remote add`, `git push`) — nunca fazer push para um remoto público sem confirmação explícita do usuário.
2. Depois de F23, todas as 24 funcionalidades de `feature_list.json` estarão `passing` — o projeto está pronto para envio.
3. Re-rodar `./init.sh` antes de qualquer entrega final, para garantir que o estado do repositório continua saudável.

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

Detalhes de cada feature (arquivos tocados, decisões, evidência) estão em `feature_list.json` (campo `evidence`) e nos commits correspondentes — não duplicados aqui para evitar desatualização.

## Em Andamento
- (nenhum item ativo no momento — F19 passou para `passing`)

## Bloqueado / Pendente de Confirmação
- **F23 (repositório Git público)** — única funcionalidade restante em `feature_list.json`. Depende de ação do usuário (conta/organização, nome, visibilidade do repositório, autorização para `git push` a um remoto público) — fora do escopo de implementação de código autônoma.
