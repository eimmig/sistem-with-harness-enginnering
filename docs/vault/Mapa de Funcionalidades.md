---
tags: [backlog]
---

# Mapa de Funcionalidades

Espelha `feature_list.json`. Uma funcionalidade ativa por vez (WIP=1); `status: passing` só depois que o comando em `verification` roda com sucesso e a evidência é registrada. **Atualize esta tabela manualmente sempre que `feature_list.json` mudar** — não há sincronização automática.

| ID | Nome | Status | Módulo | Depende de |
|---|---|---|---|---|
| F01 | Cadastro de hóspede | **passing** | [[Hóspede]] | — |
| F02 | Busca de hóspede | **passing** | [[Hóspede]] | F01 |
| F03 | Cadastro de categoria de quarto | **passing** | [[Categoria de Quarto]] | — |
| F04 | Configuração de preço por dia da semana | **passing** | [[Categoria de Quarto]] | F03 |
| F24 | Cadastro de quarto | **passing** | [[Quarto]] | F03 |
| F05 | Criação de reserva | **passing** | [[Reserva]] | F01, F24 |
| F06 | Cálculo de diária | **passing** | [[Diária]] | F04, F05 |
| F07 | Cálculo de taxa de estacionamento | **passing** | [[Taxa de Estacionamento]] | F04, F05 |
| F08 | Check-in | **passing** | [[Check-in e Check-out]] | F06, F07, F24 |
| F09 | Check-out | **passing** | [[Check-in e Check-out]] | F06, F07, F08 |
| F10 | Listagem de hóspedes no hotel | **passing** | [[Hóspede]] | F08 |
| F11 | Listagem de hóspedes sem check-in | **passing** | [[Hóspede]] | F05 |
| F13 | Tela de cadastro/busca de hóspedes | **passing** | [[Hóspede]] | F01, F02 |
| F14 | Tela de configuração de preços | **passing** | [[Categoria de Quarto]] | F03, F04 |
| F25 | Tela de gestão de quartos | **passing** | [[Quarto]] | F24 |
| F15 | Tela de criação de reserva | **passing** | [[Reserva]] | F05, F25 |
| F16 | Tela de check-in | **passing** | [[Check-in e Check-out]] | F08 |
| F17 | Tela de check-out | **passing** | [[Check-in e Check-out]] | F09 |
| F18 | Lista de hóspedes no hotel (frontend) | **passing** | [[Hóspede]] | F10 |
| F19 | Lista de hóspedes sem check-in (frontend) | **passing** | [[Hóspede]] | F11 |
| F21 | PostgreSQL local via Docker | **passing** | [[Arquitetura]] | — |
| F22 | README com setup completo | **passing** | [[Arquitetura]] | — |
| F23 | Repositório Git público | **passing** | — | — |
| F26 | Testes E2E de API - Hóspedes | **passing** | [[Hóspede]] | F01, F02, F10, F11 |
| F27 | Testes E2E de API - Categoria de Quarto | **passing** | [[Categoria de Quarto]] | F03, F04 |
| F28 | Testes E2E de API - Quarto | **passing** | [[Quarto]] | F24 |
| F29 | Testes E2E de API - Reserva/Check-in/Check-out | **passing** | [[Reserva]], [[Check-in e Check-out]] | F05, F06, F07, F08, F09 |
| F34 | Refatoração visual - Hóspedes | **passing** | [[Hóspede]] | F13, F18, F19 |
| F35 | Refatoração visual - Categoria de Quarto | **passing** | [[Categoria de Quarto]] | F14, F34 |
| F36 | Refatoração visual - Quarto | **passing** | [[Quarto]] | F25, F34 |
| F37 | Refatoração visual - Reserva/Check-in/Check-out | **passing** | [[Reserva]], [[Check-in e Check-out]] | F15, F16, F17, F34 |
| F30 | Testes E2E de UI - Hóspedes | not_started | [[Hóspede]] | F13, F18, F19, F34 |
| F31 | Testes E2E de UI - Categoria de Quarto | not_started | [[Categoria de Quarto]] | F14, F35 |
| F32 | Testes E2E de UI - Quarto | not_started | [[Quarto]] | F25, F36 |
| F33 | Testes E2E de UI - Reserva/Check-in/Check-out | not_started | [[Reserva]], [[Check-in e Check-out]] | F15, F16, F17, F37 |

## Notas de risco
F06 e F07 são as funcionalidades de maior risco (mais regras de negócio implícitas) — devem ganhar mais casos de teste do que as demais, cobrindo especialmente a transição entre dias com preços diferentes (sexta/sábado/domingo/segunda). Ver [[Diária]] e [[Taxa de Estacionamento]].

Itens do frontend dependem dos endpoints correspondentes do backend já estarem `passing`.

## Status geral (2026-07-25)
As 24 funcionalidades originais (F01-F19, F21-F25) estão `passing`. F23: repositório público em `https://github.com/eimmig/sistem-with-harness-enginnering`. Backlog expandido com F26-F37 (E2E de API/UI + redesign visual, ver D-29/D-30 em [[Arquitetura]]); F26-F29 (E2E de API) já `passing` — grupo completo. F34-F37 (redesign visual) também `passing` — grupo completo. Só falta F30-F33 (E2E de UI, Playwright), todas `not_started`.

Ver também: [[Visão Geral do Sistema]].
