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
Não implementado.
