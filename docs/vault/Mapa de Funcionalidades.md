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
| F14 | Tela de configuração de preços | not_started | [[Categoria de Quarto]] | F03, F04 |
| F25 | Tela de gestão de quartos | not_started | [[Quarto]] | F24 |
| F15 | Tela de criação de reserva | not_started | [[Reserva]] | F05, F25 |
| F16 | Tela de check-in | not_started | [[Check-in e Check-out]] | F08 |
| F17 | Tela de check-out | not_started | [[Check-in e Check-out]] | F09 |
| F18 | Lista de hóspedes no hotel (frontend) | not_started | [[Hóspede]] | F10 |
| F19 | Lista de hóspedes sem check-in (frontend) | not_started | [[Hóspede]] | F11 |
| F21 | PostgreSQL local via Docker | **passing** | [[Arquitetura]] | — |
| F22 | README com setup completo | **passing** | [[Arquitetura]] | — |
| F23 | Repositório Git público | not_started | — | — |

## Notas de risco
F06 e F07 são as funcionalidades de maior risco (mais regras de negócio implícitas) — devem ganhar mais casos de teste do que as demais, cobrindo especialmente a transição entre dias com preços diferentes (sexta/sábado/domingo/segunda). Ver [[Diária]] e [[Taxa de Estacionamento]].

Itens do frontend dependem dos endpoints correspondentes do backend já estarem `passing`.

Ver também: [[Visão Geral do Sistema]].
