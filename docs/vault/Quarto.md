---
tags: [módulo]
---

# Quarto (`Room`)

## Responsabilidade
Entidade própria (D-12 em [[Arquitetura]]): número, vínculo com [[Categoria de Quarto]], status.

## Status possíveis
`DISPONIVEL`, `SUJO`, `OCUPADO` no domínio; no código, `RoomStatus.AVAILABLE`/`DIRTY`/`OCCUPIED` (tradução D-13).

## Regras de negócio relevantes
- Check-in só pode ser realizado se o quarto estiver `DISPONIVEL`, independentemente do horário (regra #6, D-01 em [[Arquitetura]]).
- [[Reserva]] referencia um quarto específico, não apenas uma categoria (D-05).

## Funcionalidades relacionadas
[[Mapa de Funcionalidades]]: F24 (cadastro/alteração de status), F25 (tela de gestão de quartos).

## Relações
Pertence a uma [[Categoria de Quarto]]; é referenciado por uma [[Reserva]]; seu status é verificado no [[Check-in e Check-out|check-in]].

## Status atual
- **F24 (cadastro + alteração de status) — implementado e `passing`.** `Room` (entidade: `number` String, `roomCategory` ManyToOne, `status`), `RoomRepository`, `RoomController` em `backend/src/main/java/.../room/`. `POST /api/rooms` (404 se categoria não existir; quarto nasce `AVAILABLE`), `PATCH /api/rooms/{id}/status` (404 se quarto não existir). Decisão de design em D-17 (ver [[Arquitetura]]).
- **F08 (check-in)** também muda o status do quarto para `OCCUPIED` ao confirmar o check-in (ver [[Check-in e Check-out]]) — não é feito via `PATCH /api/rooms/{id}/status` diretamente, mas como efeito colateral do `ReservationController#checkIn`.
- **F09 (check-out)** muda o status do quarto para `DIRTY` ao confirmar o check-out (D-22) — precisa de limpeza antes de voltar a `AVAILABLE`; essa transição de limpeza não está no escopo de nenhuma feature ainda.
- F25 (tela de gestão de quartos) — não implementado.
