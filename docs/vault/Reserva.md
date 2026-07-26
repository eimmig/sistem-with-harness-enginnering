---
tags: [módulo]
---

# Reserva (`Reservation`)

## Responsabilidade
Vincular um [[Hóspede]] a um [[Quarto]] específico, com data de entrada/saída previstas (D-05 em [[Arquitetura]]).

## Regras de negócio relevantes
- Referencia um quarto específico, não apenas uma [[Categoria de Quarto]] — é do quarto que vem, indiretamente, a categoria usada no cálculo da [[Diária]].
- É o gatilho para as listagens de hóspedes "sem check-in" (regra #11).

## Funcionalidades relacionadas
[[Mapa de Funcionalidades]]: F05 (criação), F15 (tela de criação), F11 (listagem de hóspedes sem check-in), F29 (E2E de API, junto com [[Check-in e Check-out]]), F37 (refatoração visual, junto com [[Check-in e Check-out]]).

## Relações
Liga [[Hóspede]] e [[Quarto]]. É a base sobre a qual [[Check-in e Check-out]] atua, e cujo total é calculado por [[Diária]] + [[Taxa de Estacionamento]].

## Status atual
- **F05 (criação) — implementado e `passing`.** `Reservation` (entidade: `guest`, `room`, `expectedCheckIn`/`expectedCheckOut`, `parkingRequested`), `ReservationRepository`, `ReservationController` (`POST /api/reservations`) em `backend/src/main/java/.../reservation/`. Valida existência de hóspede/quarto (404) e que check-out é depois do check-in (400) — **não** valida status do quarto nesse momento, só no check-in (D-18 em [[Arquitetura]]).
- **`parkingRequested`** (boolean, default `false`) foi adicionado em F07 — captura "hóspede tem carro e usa vaga" (regra #5) já na criação da reserva, reaproveitado por [[Taxa de Estacionamento]] (D-20).
- **`actualCheckIn`** (`LocalDateTime`, nullable) foi adicionado em F08 — registra quando o check-in de fato ocorreu (ver [[Check-in e Check-out]], D-21).
- **`actualCheckOut`** (`LocalDateTime`, nullable) foi adicionado em F09 — registra quando o check-out de fato ocorreu (D-22). Junto com `actualCheckIn`, viabiliza as listagens F10 (hóspede no hotel = `actualCheckIn` preenchido e `actualCheckOut` nulo) e F11 (sem check-in = `actualCheckIn` nulo).
- F10/F11 (listagens de hóspede, backend) — `passing`, ver [[Hóspede]].
- **F15 (tela de criação) — implementada e `passing`.** `ReservationFormComponent` em `frontend/src/app/features/reservation/`: busca hóspede por nome (reaproveita `GuestService.search()` de [[Hóspede]]), seleciona quarto (reaproveita `RoomService.list()` de [[Quarto]], sem filtrar por status — D-26 em [[Arquitetura]]), datas via `<input type="datetime-local">`, checkbox de estacionamento. Composto em `ReservationsPageComponent` (rota `/reservations`).
- **F37 (refatoração visual) — implementado e `passing`.** Reverte D-26: `<input type="datetime-local">` trocado por `MatDatepicker` (campo de data) + `<input type="time">` separado (campo de hora), com valor padrão pré-preenchido conforme a restrição #1 (`14:00` para check-in, `12:00` para check-out) que o atendente pode ajustar. O `FormGroup` passa a ter 4 controles (`expectedCheckInDate`/`Time`, `expectedCheckOutDate`/`Time`) em vez de 2 strings; `combineDateAndTime()` os funde no mesmo formato `yyyy-MM-ddTHH:mm` que o backend já esperava — nenhuma mudança de contrato HTTP (D-30). Layout reaproveita `form-row`/`mat-card` de F34-F36.
