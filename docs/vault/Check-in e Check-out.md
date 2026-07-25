---
tags: [módulo]
---

# Check-in e Check-out (`CheckIn` / `CheckOut`)

## Responsabilidade
Efetivar a entrada e a saída de um hóspede em uma [[Reserva]], aplicando as regras de horário e status do [[Quarto]].

## Regras de negócio relevantes
- **Check-in**: a partir das 14h é permitido diretamente; antes disso, o sistema exibe um aviso e exige confirmação explícita do atendente (não apenas informativo) — regra #6, D-01 em [[Arquitetura]].
- **Check-in**: em qualquer horário, só pode ser realizado se o quarto estiver `DISPONIVEL` (independe do horário; `SUJO`/`OCUPADO` bloqueia) — D-01.
- **Check-out**: até 12h sem custo adicional; após esse horário, cobra-se 50% do valor da [[Diária]] vigente, respeitando dia útil/fim de semana (regra #7).
- **Check-out**: exibir o detalhamento completo do valor total antes de confirmar — diárias + [[Taxa de Estacionamento]] + eventual taxa de atraso (regra #8).

## Funcionalidades relacionadas
[[Mapa de Funcionalidades]]: F08 (check-in), F09 (check-out), F16 (tela de check-in), F17 (tela de check-out). Ambas dependem de F06 e F07 já estarem `passing`.

## Status atual
- **F08 (check-in) — implementado e `passing`.** `ReservationController#checkIn` (`POST /api/reservations/{id}/check-in`) em `backend/src/main/java/.../reservation/`. 404 se a reserva não existir; 409 se já tem check-in feito ou se o quarto não está `AVAILABLE`; 400 se antes das 14h sem `confirmedByAttendant=true` no corpo; ao confirmar, grava `Reservation.actualCheckIn` (novo campo) e muda o [[Quarto]] para `OCCUPIED`. "Agora" vem de um `Clock` injetável (`ClockConfig`), não de `LocalDateTime.now()` direto — permite testes determinísticos (D-21 em [[Arquitetura]]).
- **F09 (check-out) — implementado e `passing`.** `ReservationController#checkOut` (`POST /api/reservations/{id}/check-out`). 404 se a reserva não existir; 409 se ainda não teve check-in ou já teve check-out; senão calcula e devolve o detalhamento completo (diária + estacionamento + taxa de atraso + total — regra #8), reaproveitando [[Diária]] (`DailyRateService`) e [[Taxa de Estacionamento]] (`ParkingFeeService`) com `actualCheckIn` até "agora". Se após 12h, cobra 50% do preço da última diária hospedada (regra #7 — ver D-22 em [[Arquitetura]] para qual diária conta como "vigente"). Ao confirmar, grava `Reservation.actualCheckOut` e muda o [[Quarto]] para `DIRTY` (não `AVAILABLE` direto — precisa de limpeza). Chamada única, sem endpoint de prévia separado — "exibir antes de confirmar" é responsabilidade do frontend (F17).
- F16/F17 (telas) — não implementados.
