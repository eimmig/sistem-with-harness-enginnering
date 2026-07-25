---
tags: [domínio, glossário]
---

# Glossário de Domínio

Código-fonte (classes, variáveis, mensagens de erro) é escrito em inglês; a documentação do domínio permanece em português (ver DECISIONS.md D-13). Esta tabela é a referência de tradução — mantenha consistente entre sessões.

| Português | Inglês | Nota |
|---|---|---|
| [[Hóspede]] | `Guest` | nome, documento, telefone |
| [[Reserva]] | `Reservation` | hóspede + quarto específico + datas previstas |
| [[Categoria de Quarto]] | `RoomCategory` | ex.: Standard, Luxo; dona do preço por dia da semana |
| [[Quarto]] | `Room` | número, categoria, status (`DISPONIVEL`/`SUJO`/`OCUPADO`) |
| [[Diária]] | `DailyRate` | valor de uma diária individual (um dia da semana) |
| [[Taxa de Estacionamento]] | `ParkingFee` | cobrada por dia de estadia, se houver carro |
| Check-in / Check-out | `CheckIn` / `CheckOut` | já em inglês |

Ver [[Mapa de Funcionalidades]] para onde cada conceito aparece no backlog.
