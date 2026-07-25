# Progresso do Projeto

## Última Atualização
2026-07-25 — F14 (Tela de configuração de preços) implementada e passando: backend ganhou `GET /api/room-categories` (D-24, necessário para a tela listar categorias existentes); `RoomCategoryFormComponent` (cadastro) + `RoomCategoryPriceComponent` (seleciona categoria, configura os 7 dias) + `RoomCategoriesPageComponent` (rota `/room-categories`); 22/22 testes Karma passando, `ng build` sem erros.

## Objetivo Atual
Backend de negócio central completo (F01–F11, F24). F13 e F14 são as telas de frontend concluídas até agora. Restam: F16, F17, F18, F19, F25 (sem dependências pendentes) e F15 (depende de F25); e F23 (repositório Git público — fora do escopo de automação, requer ação do usuário).

## Próximo Passo Recomendado
1. Seguir para as próximas telas do frontend, na ordem do `feature_list.json`: F25 (gestão de quartos) → F15 (criação de reserva, depende de F25) → F16 (check-in) → F17 (check-out) → F18 (lista de hóspedes no hotel) → F19 (lista de hóspedes sem check-in).
2. Reaproveitar a infraestrutura já criada em F13/F14 (`provideHttpClient`, `provideAnimationsAsync`, proxy `/api`, padrão de pastas `features/<domínio>/`) — não deve ser necessário repetir esse setup.
3. Cada tela consome os endpoints já `passing` do backend correspondente. Se faltar um endpoint de listagem que a própria tela precisa (como aconteceu em F14 com `GET /api/room-categories`), adicioná-lo é aceitável dentro do escopo da feature de tela — não é scope creep, é o que a tela exige (ver D-24). F25 (quartos) provavelmente vai precisar de `GET /api/rooms` e `GET /api/room-categories` (já existe); F15 (reserva) provavelmente vai precisar de listas de hóspedes/quartos disponíveis.
4. F23 (repositório Git público) não pode ser concluída autonomamente — publicar um repositório público e compartilhar o link é uma decisão/ação do usuário (credenciais, conta, visibilidade). Sinalizar como bloqueio quando for a única pendência restante.
5. Re-rodar `./init.sh` antes de considerar cada funcionalidade concluída.

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

Detalhes de cada feature (arquivos tocados, decisões, evidência) estão em `feature_list.json` (campo `evidence`) e nos commits correspondentes — não duplicados aqui para evitar desatualização.

## Em Andamento
- (nenhum item ativo no momento — F11 passou para `passing`)

## Bloqueado / Pendente de Confirmação
- F23 (repositório Git público) depende de ação do usuário fora do escopo de implementação de código.
