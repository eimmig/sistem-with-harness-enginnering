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
[[Mapa de Funcionalidades]]: F01 (cadastro), F02 (busca), F10 (listagem no hotel), F11 (listagem sem check-in), F13 (tela cadastro/busca), F18/F19 (telas de listagem).

## Relações
Um hóspede faz uma ou mais [[Reserva|reservas]]; cada reserva é o elo entre hóspede e [[Quarto]].

## Status atual
- **F01 (cadastro) — implementado e `passing`.** `Guest` (entidade), `GuestRepository`, `GuestController` (`POST /api/guests`) em `backend/src/main/java/.../guest/`. Validação `@NotBlank` em nome/documento/telefone.
- **F02 (busca) — implementado e `passing`.** `GuestController#search` (`GET /api/guests?name=&document=&phone=`), filtros opcionais combinados por AND, partial match case-insensitive, via `GuestSpecifications` + `GuestRepository extends JpaSpecificationExecutor<Guest>`. Sem filtros, retorna todos os hóspedes. Decisão de design em D-15 (ver [[Arquitetura]]).
- **F10 (listagem no hotel) — implementado e `passing`.** `GuestController#guestsInHotel` (`GET /api/guests/in-hotel`) — injeta `ReservationRepository` (dependência cruzada entre módulos `guest`/`reservation`, aceita conscientemente), consulta `findByActualCheckInIsNotNullAndActualCheckOutIsNull()` e mapeia cada [[Reserva]] para o hóspede correspondente.
- **F11 (listagem sem check-in) — implementado e `passing`.** `GuestController#guestsWithoutCheckIn` (`GET /api/guests/without-check-in`), mesma dependência de `ReservationRepository`, consulta `findByActualCheckInIsNull()`.
- F13/F18/F19 (telas) — não implementados. Com F11, o backend de negócio central (F01–F11, F24) está completo.
