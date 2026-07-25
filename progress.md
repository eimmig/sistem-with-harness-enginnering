# Progresso do Projeto

## Última Atualização
2026-07-25 — reconciliação do harness com o `skills/harness-creator` do repositório do curso (`init.sh`, `feature_list.json`, `session-handoff.md`).

## Objetivo Atual
Fase de **inicialização** concluída (nenhuma funcionalidade de negócio implementada ainda — por design, ver `AGENTS.md`). Aguardando confirmação das suposições de negócio (`DECISIONS.md`: D-01, D-02, D-03) antes de começar `F01`.

## Próximo Passo Recomendado
1. Confirmar (ou ajustar) as suposições D-01, D-02 e D-03 com o solicitante do desafio.
2. Rodar `./init.sh` para revalidar o ambiente do zero.
3. Marcar `F01` como `active` em `feature_list.json` e começar a implementação (uma funcionalidade por vez).

## Estado Atual
- Status dos testes: `./mvnw test` (backend, via H2) e `npm run test:ci` (frontend, via Chrome headless) passando do zero — confirmado por `./init.sh`.
- Backend validado também contra o PostgreSQL real do `docker-compose.yml` (sobe sem erros na porta 8080).

## Concluído
- [x] Repositório Git inicializado (branch `main`)
- [x] `AGENTS.md`, `DECISIONS.md`, `progress.md`, `feature_list.json`, `session-handoff.md`, `init.sh` criados
- [x] Esqueleto do backend (Spring Boot 4.1 + Java 17 + PostgreSQL/H2) — `./mvnw test` passando
- [x] Esqueleto do frontend (Angular 19 + Angular Material) — `npm run test:ci` passando
- [x] `docker-compose.yml` para o PostgreSQL local — validado end-to-end (backend conecta e sobe)
- [x] `README.md` com instruções de setup testadas manualmente
- [x] Harness validado com `skills/harness-creator/scripts/validate-harness.mjs`

## Em Andamento
- (nenhum item ativo no momento — pronto para começar F01)

## Bloqueado / Pendente de Confirmação
- Três suposições de regra de negócio precisam de confirmação do solicitante do desafio antes da implementação das regras de precificação (ver `DECISIONS.md`: D-01, D-02, D-03).
