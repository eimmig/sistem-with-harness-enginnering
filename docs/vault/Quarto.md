---
tags: [módulo]
---

# Quarto (`Room`)

## Responsabilidade
Entidade própria (D-12 em [[Arquitetura]]): número, vínculo com [[Categoria de Quarto]], status.

## Status possíveis
`DISPONIVEL`, `SUJO`, `OCUPADO`.

## Regras de negócio relevantes
- Check-in só pode ser realizado se o quarto estiver `DISPONIVEL`, independentemente do horário (regra #6, D-01 em [[Arquitetura]]).
- [[Reserva]] referencia um quarto específico, não apenas uma categoria (D-05).

## Funcionalidades relacionadas
[[Mapa de Funcionalidades]]: F24 (cadastro/alteração de status), F25 (tela de gestão de quartos).

## Relações
Pertence a uma [[Categoria de Quarto]]; é referenciado por uma [[Reserva]]; seu status é verificado no [[Check-in e Check-out|check-in]].

## Status atual
Não implementado.
