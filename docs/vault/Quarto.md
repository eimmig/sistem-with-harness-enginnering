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
[[Mapa de Funcionalidades]]: F24 (cadastro/alteração de status), F25 (tela de gestão de quartos), F28 (E2E de API).

## Relações
Pertence a uma [[Categoria de Quarto]]; é referenciado por uma [[Reserva]]; seu status é verificado no [[Check-in e Check-out|check-in]].

## Status atual
- **F24 (cadastro + alteração de status) — implementado e `passing`.** `Room` (entidade: `number` String, `roomCategory` ManyToOne, `status`), `RoomRepository`, `RoomController` em `backend/src/main/java/.../room/`. `POST /api/rooms` (404 se categoria não existir; quarto nasce `AVAILABLE`), `PATCH /api/rooms/{id}/status` (404 se quarto não existir). Decisão de design em D-17 (ver [[Arquitetura]]).
- **F08 (check-in)** também muda o status do quarto para `OCCUPIED` ao confirmar o check-in (ver [[Check-in e Check-out]]) — não é feito via `PATCH /api/rooms/{id}/status` diretamente, mas como efeito colateral do `ReservationController#checkIn`.
- **F09 (check-out)** muda o status do quarto para `DIRTY` ao confirmar o check-out (D-22) — precisa de limpeza antes de voltar a `AVAILABLE`; essa transição de limpeza não está no escopo de nenhuma feature ainda.
- **`GET /api/rooms`** (listagem) foi adicionado em F25 (D-25) — F24 originalmente só tinha `POST`/`PATCH`.
- **F25 (tela de gestão de quartos) — implementada e `passing`.** `RoomFormComponent` (cadastro, seleciona categoria via `RoomCategoryService`) + `RoomListComponent` (tabela com troca de status inline), compostos em `RoomsPageComponent` (rota `/rooms`), em `frontend/src/app/features/room/`.
- **F28 (E2E de API) — implementado e `passing`.** `RoomE2ETest` em `backend/src/test/java/.../e2e/`, mesma infra Testcontainers/`TestRestTemplate` de F26/F27. Cobre cadastro (nasce `AVAILABLE`, 404 se categoria não existir), listagem, alteração de status (persistida e refletida em novo `GET`) e 404 para quarto inexistente.
