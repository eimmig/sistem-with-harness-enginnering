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
- F02 (busca), F10/F11 (listagens), F13/F18/F19 (telas) — não implementados.
