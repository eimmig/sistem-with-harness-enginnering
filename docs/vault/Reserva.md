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
[[Mapa de Funcionalidades]]: F05 (criação), F15 (tela de criação), F11 (listagem de hóspedes sem check-in).

## Relações
Liga [[Hóspede]] e [[Quarto]]. É a base sobre a qual [[Check-in e Check-out]] atua, e cujo total é calculado por [[Diária]] + [[Taxa de Estacionamento]].

## Status atual
- **F05 (criação) — implementado e `passing`.** `Reservation` (entidade: `guest`, `room`, `expectedCheckIn`/`expectedCheckOut`, `parkingRequested`), `ReservationRepository`, `ReservationController` (`POST /api/reservations`) em `backend/src/main/java/.../reservation/`. Valida existência de hóspede/quarto (404) e que check-out é depois do check-in (400) — **não** valida status do quarto nesse momento, só no check-in (D-18 em [[Arquitetura]]).
- **`parkingRequested`** (boolean, default `false`) foi adicionado em F07 — captura "hóspede tem carro e usa vaga" (regra #5) já na criação da reserva, reaproveitado por [[Taxa de Estacionamento]] (D-20).
- **`actualCheckIn`** (`LocalDateTime`, nullable) foi adicionado em F08 — registra quando o check-in de fato ocorreu (ver [[Check-in e Check-out]], D-21). Ainda falta `actualCheckOut`, que deve entrar em F09 e viabilizar as listagens F10/F11.
- F15 (tela de criação), F11 (listagem sem check-in) — não implementados.
