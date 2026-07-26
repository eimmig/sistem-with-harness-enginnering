---
tags: [módulo]
---

# Hóspede (`Guest`)

## Responsabilidade
Cadastro e busca de hóspedes; listagens derivadas do estado das suas [[Reserva|reservas]].

## Dados mínimos
Nome, documento, telefone (regra #12 em [[Visão Geral do Sistema]]).

## Regras de negócio relevantes
- Buscar por nome, documento e telefone (regra #9).
- Listar hóspedes atualmente hospedados — check-in feito, sem check-out (regra #10).
- Listar hóspedes com reserva mas sem check-in realizado (regra #11).

## Funcionalidades relacionadas
[[Mapa de Funcionalidades]]: F01 (cadastro), F02 (busca), F10 (listagem no hotel), F11 (listagem sem check-in), F13 (tela cadastro/busca), F18/F19 (telas de listagem), F26 (E2E de API), F34 (refatoração visual), F30 (E2E de UI).

## Relações
Um hóspede faz uma ou mais [[Reserva|reservas]]; cada reserva é o elo entre hóspede e [[Quarto]].

## Status atual
- **F01 (cadastro) — implementado e `passing`.** `Guest` (entidade), `GuestRepository`, `GuestController` (`POST /api/guests`) em `backend/src/main/java/.../guest/`. Validação `@NotBlank` em nome/documento/telefone.
- **F02 (busca) — implementado e `passing`.** `GuestController#search` (`GET /api/guests?name=&document=&phone=`), filtros opcionais combinados por AND, partial match case-insensitive, via `GuestSpecifications` + `GuestRepository extends JpaSpecificationExecutor<Guest>`. Sem filtros, retorna todos os hóspedes. Decisão de design em D-15 (ver [[Arquitetura]]).
- **F10 (listagem no hotel) — implementado e `passing`.** `GuestController#guestsInHotel` (`GET /api/guests/in-hotel`) — injeta `ReservationRepository` (dependência cruzada entre módulos `guest`/`reservation`, aceita conscientemente), consulta `findByActualCheckInIsNotNullAndActualCheckOutIsNull()` e mapeia cada [[Reserva]] para o hóspede correspondente.
- **F11 (listagem sem check-in) — implementado e `passing`.** `GuestController#guestsWithoutCheckIn` (`GET /api/guests/without-check-in`), mesma dependência de `ReservationRepository`, consulta `findByActualCheckInIsNull()`.
- **F13 (tela cadastro/busca) — implementada e `passing`.** `GuestFormComponent` + `GuestSearchComponent`, compostos em `GuestsPageComponent` (rota `/guests`), em `frontend/src/app/features/guest/`. Consomem `POST`/`GET /api/guests`. Infraestrutura de frontend criada nesta feature (proxy, HttpClient, animações — D-23 em [[Arquitetura]]).
- **F18 (lista no hotel, frontend) — implementada e `passing`.** `GuestsInHotelComponent` (rota `/guests-in-hotel`) em `frontend/src/app/features/guest/guests-in-hotel/`, consome `GET /api/guests/in-hotel` (já existente desde F10) — nenhuma mudança de backend foi necessária.
- **F19 (lista sem check-in, frontend) — implementada e `passing`.** `GuestsWithoutCheckinComponent` (rota `/guests-without-check-in`) em `frontend/src/app/features/guest/guests-without-checkin/`, consome `GET /api/guests/without-check-in` (já existente desde F11) — nenhuma mudança de backend foi necessária. Com esta feature, o módulo Hóspede está com todas as suas funcionalidades (F01, F02, F10, F11, F13, F18, F19) `passing`.
- **F26 (E2E de API) — implementado e `passing`.** `GuestE2ETest` em `backend/src/test/java/.../e2e/`: `@SpringBootTest(webEnvironment=RANDOM_PORT)` + `@Testcontainers` com `PostgreSQLContainer` (`@ServiceConnection`) e `TestRestTemplate` (`@AutoConfigureTestRestTemplate`), sem MockMvc/H2/mocks. Cobre cadastro, busca por nome/documento/telefone, listagem no hotel (via fluxo real de reserva + check-in) e listagem sem check-in. Ver D-29 (escopo E2E) e D-32 (por que o pacote `e2e` é excluído do `./mvnw test` padrão) em [[Arquitetura]].
- **F34 (refatoração visual) — implementado e `passing`.** Primeira feature do grupo de redesign (D-30): instala `ngx-mask@19.0.7` (compatível com Angular 19 -- a versão mais recente do pacote exige Angular 22) e registra `provideEnvironmentNgxMask()` em `app.config.ts`. Estabelece o layout compartilhado em `styles.scss` (`.page-header`, `.section-card`, `.form-row`, `.form-field-compact`, `.data-table`, `.empty-state`), reaproveitado por F35-F37. `GuestFormComponent`/`GuestSearchComponent` ganham máscara de CPF (`000.000.000-00`) no campo documento e de telefone BR (`(00) 00000-0000`) -- `dropSpecialCharacters` (default `true` do ngx-mask) mantém o valor do `FormControl` como dígitos puros, então o payload enviado ao backend não muda. `GuestsPageComponent`, `GuestsInHotelComponent` e `GuestsWithoutCheckinComponent` passam a usar `mat-card` para agrupar cada seção visualmente.
- **F30 (E2E de UI) — implementado e `passing`.** Primeira feature do grupo de E2E de UI (D-29): instala `@playwright/test@1.62.0` e cria `frontend/playwright.config.ts` (D-38 em [[Arquitetura]]) com `webServer` array subindo backend real (`mvnw.cmd spring-boot:run`) + Postgres real (docker-compose) e frontend real (`ng serve`) juntos. `frontend/e2e/guest-flow.e2e.spec.ts` cobre cadastro pela tela, atualização automática da busca via `refreshSignal`, busca filtrada por nome/documento/telefone isoladamente, busca sem resultado, e navegação para `/guests-in-hotel`/`/guests-without-check-in` confirmando que as telas renderizam (tabela ou empty-state). Dados de teste gerados com timestamp para não colidir entre execuções, já que o Postgres real persiste dados entre runs (sem reset de banco entre testes).
