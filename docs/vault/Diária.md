---
tags: [módulo]
---

# Diária (`DailyRate`)

## Responsabilidade
Calcular o valor de uma [[Reserva]] somando o preço de cada dia individual da estadia, conforme a [[Categoria de Quarto]] do quarto reservado.

## Regras de negócio relevantes
- Diária padrão: check-in às 14h de um dia → check-out até 12h do dia seguinte (regra #1).
- Fim de semana **não é um bloco especial de cálculo** (D-02 em [[Arquitetura]]): sexta 14h→sábado 12h, sábado 14h→domingo 12h, domingo 14h→segunda 12h são três diárias individuais, somadas no fechamento (regra #3). A diferença entre dia útil e fim de semana está só no valor configurado, não na fórmula.
- Valor configurável por categoria e por dia da semana (regra #4) — ver [[Categoria de Quarto]].
- Após check-out às 12h, cobra-se 50% do valor da diária vigente (regra #7).

## Risco
Módulo de maior risco do backlog junto com [[Taxa de Estacionamento]] — mais casos de teste devem cobrir a transição entre dias com preços diferentes (sexta/sábado/domingo/segunda). Ver notas em [[Mapa de Funcionalidades]].

## Funcionalidades relacionadas
[[Mapa de Funcionalidades]]: F06 (cálculo de diária), depende de F04 e F05.

## Status atual
- **F06 (cálculo) — implementado e `passing`.** `DailyRateService` (pacote `dailyrate`, serviço puro, sem endpoint HTTP próprio nesta etapa) em `backend/src/main/java/.../dailyrate/`. `calculate(RoomCategory, checkIn, checkOut)`: número de noites via diferença de datas de calendário; cada noite atribuída ao dia da semana em que **começa** (a diária sexta 14h→sábado 12h é "sexta"); preço de `RoomCategory.prices`. Decisão de design em D-19 (ver [[Arquitetura]]). Ainda não consumido por nenhum controller — será usado por F08/F09 (check-in/check-out).
