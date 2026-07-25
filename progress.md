# Progresso do Projeto

## Última Atualização
2026-07-25 — decisões de negócio revisadas e confirmadas (D-01, D-02, D-03, D-05), entidade `Quarto` (D-12) e código em inglês (D-13) adicionadas; `feature_list.json` reescrito para refletir isso; `AGENTS.md` renomeado para `CLAUDE.md`.

## Objetivo Atual
Fase de **inicialização** concluída (nenhuma funcionalidade de negócio implementada ainda — por design, ver `CLAUDE.md`). Todas as decisões de negócio confirmadas, `feature_list.json` atualizado. Pronto para começar `F01`.

## Próximo Passo Recomendado
1. Marcar `F01` como `active` em `feature_list.json` e começar a implementação (uma funcionalidade por vez).
2. Re-rodar `./init.sh` antes de considerar cada funcionalidade concluída.

## Estado Atual
- Status dos testes: `./mvnw test` (backend, via H2) e `npm run test:ci` (frontend, via Chrome headless) passando do zero — confirmado por `./init.sh` em 2026-07-25.
- Backend validado também contra o PostgreSQL real do `docker-compose.yml` (sobe sem erros na porta 8080).

## Concluído
- [x] Repositório Git inicializado (branch `main`)
- [x] `CLAUDE.md`, `DECISIONS.md`, `progress.md`, `feature_list.json`, `session-handoff.md`, `init.sh` criados
- [x] Esqueleto do backend (Spring Boot 4.1 + Java 17 + PostgreSQL/H2) — `./mvnw test` passando
- [x] Esqueleto do frontend (Angular 19 + Angular Material) — `npm run test:ci` passando
- [x] `docker-compose.yml` para o PostgreSQL local — validado end-to-end (backend conecta e sobe)
- [x] `README.md` com instruções de setup testadas manualmente
- [x] Decisões de negócio D-01, D-02, D-03, D-05 confirmadas; D-12 e D-13 registradas; `feature_list.json` atualizado (F24/F25 adicionados, identificadores em inglês)

## Em Andamento
- (nenhum item ativo no momento — pronto para começar F01)

## Bloqueado / Pendente de Confirmação
- (nenhum bloqueio no momento)
