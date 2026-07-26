---
tags: [módulo]
---

# Taxa de Estacionamento (`ParkingFee`)

## Responsabilidade
Calcular a taxa de estacionamento cobrada por dia de estadia, quando o hóspede tem carro e usa vaga.

## Regras de negócio relevantes
- R$ 15,00 em diária de dia útil, R$ 20,00 em diária de fim de semana (regra #5).
- Cobrada **por dia**, não em bloco — consequência de D-02: como o fim de semana não é um bloco especial, a taxa usa o valor correspondente a cada dia individual (D-03 em [[Arquitetura]]).

## Risco
Módulo de maior risco do backlog junto com [[Diária]] — mesma lógica de dia útil/fim de semana precisa ser coberta por teste dia a dia.

## Funcionalidades relacionadas
[[Mapa de Funcionalidades]]: F07 (cálculo de taxa de estacionamento), depende de F04 e F05.

## Status atual
- **F07 (cálculo) — implementado e `passing`.** `ParkingFeeService` (pacote `parkingfee`, serviço puro, sem endpoint próprio ainda) em `backend/src/main/java/.../parkingfee/`. `calculate(parkingRequested, checkIn, checkOut)`: R$ 15,00/noite em dia útil (segunda-sexta), R$ 20,00/noite em fim de semana (sábado-domingo), zero se `parkingRequested=false`. A informação "hóspede tem carro e usa vaga" agora mora em `Reservation.parkingRequested` (D-20 em [[Arquitetura]]) — capturada na criação da reserva ([[Reserva]]), não no check-in.
- **Ajuste (D-39, 2026-07-26)**: mesma correção de [[Diária]] — check-out no mesmo dia calendário do check-in cobra a taxa mínima de 1 dia em vez de rejeitar.
