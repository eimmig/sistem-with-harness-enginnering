---
tags: [módulo]
---

# Categoria de Quarto (`RoomCategory`)

## Responsabilidade
Agrupar [[Quarto|quartos]] (ex.: Standard, Luxo) e ser a dona do preço configurável da [[Diária]] por dia da semana.

## Regras de negócio relevantes
- Diária de dia útil (segunda a sexta) tem valor configurável por categoria (regra #2).
- O valor da diária é configurável por categoria **e por dia da semana**, via tela de configuração — não é constante fixa no código (regra #4).
- A estadia de fim de semana não é um bloco especial de cálculo (D-02 em [[Arquitetura]]) — cada dia individual tem seu próprio preço.

## Funcionalidades relacionadas
[[Mapa de Funcionalidades]]: F03 (cadastro), F04 (configuração de preço por dia da semana), F14 (tela de configuração de preços).

## Relações
Cada [[Quarto]] pertence a uma categoria. A categoria é a fonte do valor usado no cálculo de [[Diária]].

## Status atual
Não implementado.
