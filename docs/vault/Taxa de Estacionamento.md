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
Não implementado.
