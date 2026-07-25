# Progresso do Projeto

## Estado Atual
- Fase: **inicialização** (nenhuma funcionalidade de negócio implementada ainda — por design, ver `AGENTS.md`).
- Último commit: (ainda não realizado nesta sessão)
- Status dos testes: backend e frontend ainda serão escrita/validado nesta sessão de inicialização.

## Concluído
- [x] Repositório Git inicializado (branch `main`)
- [x] `AGENTS.md`, `DECISIONS.md`, `PROGRESS.md`, `FEATURES.md` criados

## Em Andamento
- [ ] Esqueleto do backend (Spring Boot + PostgreSQL + JUnit)
- [ ] Esqueleto do frontend (Angular + Material)
- [ ] `docker-compose.yml` para o PostgreSQL local
- [ ] `README.md` com instruções de setup

## Bloqueado / Pendente de Confirmação
- Três suposições de regra de negócio precisam de confirmação do solicitante do desafio antes da implementação das regras de precificação (ver `DECISIONS.md`: D-01, D-02, D-03).

## Próximos Passos
1. Gerar esqueleto do backend via Spring Initializr (web, data-jpa, postgresql, validation, lombok).
2. Gerar esqueleto do frontend via Angular CLI + `ng add @angular/material`.
3. Escrever `docker-compose.yml` com o serviço PostgreSQL.
4. Validar que `./mvnw test` e `npm test` passam do zero (checklist de prontidão).
5. Escrever `README.md` com os comandos reais e confirmados.
6. Commit inicial do checkpoint de inicialização.
7. Só então começar a implementar `FEATURES.md`, uma funcionalidade por vez (WIP=1), começando por F01.
