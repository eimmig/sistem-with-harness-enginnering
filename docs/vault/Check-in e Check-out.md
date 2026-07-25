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
Não implementado.
