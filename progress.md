# Progresso do Projeto

## Última Atualização
2026-07-25 — F01 (Cadastro de hóspede) implementado e passando: entidade `Guest`, `GuestRepository`, `GuestController` (POST `/api/guests`), `GuestControllerTest` + `GuestRepositoryTest` verdes.

## Objetivo Atual
Primeira funcionalidade de negócio (F01) concluída. Pronto para escolher a próxima funcionalidade `not_started` sem dependências pendentes (candidatas: F02, F03).

## Próximo Passo Recomendado
1. Escolher a próxima funcionalidade em `feature_list.json` (F02 depende só de F01, que já é `passing`; F03 não tem dependências) e marcar `active`.
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
- [x] Swagger/OpenAPI (springdoc 3.0.3) configurado no backend (`OpenApiConfig`), validado com `./mvnw test`; D-14 registrada
- [x] Base de conhecimento Obsidian criada em `docs/vault/` (visão geral, glossário, arquitetura, mapa de funcionalidades, uma nota por módulo)
- [x] **F01 — Cadastro de hóspede**: `Guest` (entidade), `GuestRepository`, `GuestRequest`/`GuestResponse` (DTOs com Bean Validation), `GuestController` (`POST /api/guests`); `GuestControllerTest` (4 testes) + `GuestRepositoryTest` (2 testes) passando; evidência registrada em `feature_list.json`.

## Em Andamento
- (nenhum item ativo no momento — F01 passou para `passing`)

## Bloqueado / Pendente de Confirmação
- (nenhum bloqueio no momento)
