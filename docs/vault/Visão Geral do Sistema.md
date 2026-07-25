---
tags: [visão-geral]
---

# Visão Geral do Sistema

Sistema de gestão de hóspedes para um hotel: cadastro de hóspedes, reservas, check-in e check-out, com cálculo automático de diárias, taxa de estacionamento e taxa de atraso na saída.

Especificação original: `Desafio Full-Stack - TA 11.pdf` (raiz do repositório).

## Regras de negócio obrigatórias

1. Uma "diária" padrão vai do check-in às 14h de um dia ao check-out até as 12h do dia seguinte.
2. Diária de dia útil (segunda a sexta) tem valor configurável por categoria de quarto.
3. A estadia de fim de semana é composta por diárias individuais (sexta→sábado, sábado→domingo, domingo→segunda) somadas no fechamento — não é tratada como bloco especial de cálculo (ver [[Arquitetura#D-02 — Diária de fim de semana|D-02]]).
4. O valor da diária é configurável por [[Categoria de Quarto]] e por dia da semana, via tela de configuração.
5. Taxa de estacionamento (se o hóspede tiver carro e usar vaga): R$ 15,00 em diária de dia útil, R$ 20,00 em diária de fim de semana. Ver [[Taxa de Estacionamento]].
6. Check-in a partir das 14h é permitido diretamente; antes disso exige aviso + confirmação explícita do atendente. Em qualquer horário, só é permitido se o [[Quarto]] estiver `DISPONIVEL`. Ver [[Check-in e Check-out]].
7. Check-out até as 12h sem custo adicional; após esse horário, cobra-se 50% do valor da diária vigente.
8. No checkout, exibir o detalhamento completo do valor total antes de confirmar (diárias + estacionamento + eventual taxa de atraso).
9. Buscar [[Hóspede|hóspedes]] por nome, documento e telefone.
10. Listar hóspedes atualmente hospedados (check-in feito, sem check-out).
11. Listar hóspedes com reserva mas sem check-in realizado.
12. Cadastro de hóspede: nome, documento, telefone (mínimo).
13. Testes unitários obrigatórios nos dois lados, cobrindo requisitos funcionais e regras de negócio.

## Escopo de trabalho
- Uma funcionalidade ativa por vez (`status: "active"` em `feature_list.json`, WIP=1) — ver [[Mapa de Funcionalidades]].
- [[Reserva]] referencia um [[Quarto]] específico, não apenas uma [[Categoria de Quarto]].
- Código-fonte em inglês; documentação do domínio em português — ver [[Glossário de Domínio]].

Ver também: [[Arquitetura]].
