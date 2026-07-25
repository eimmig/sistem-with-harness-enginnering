# Progresso do Projeto

## Estado Atual
- Fase: **inicialização concluída** (nenhuma funcionalidade de negócio implementada ainda — por design, ver `AGENTS.md`).
- Último commit: `79d964d` — chore: inicialização do harness do projeto
- Status dos testes: `./mvnw test` (backend, via H2) e `npm run test:ci` (frontend, via Chrome headless) passando do zero.
- Backend validado também contra o PostgreSQL real do `docker-compose.yml` (sobe sem erros na porta 8080).

## Concluído
- [x] Repositório Git inicializado (branch `main`)
- [x] `AGENTS.md`, `DECISIONS.md`, `PROGRESS.md`, `FEATURES.md` criados
- [x] Esqueleto do backend (Spring Boot 4.1 + Java 17 + PostgreSQL/H2) — `./mvnw test` passando
- [x] Esqueleto do frontend (Angular 19 + Angular Material) — `npm run test:ci` passando
- [x] `docker-compose.yml` para o PostgreSQL local — validado end-to-end (backend conecta e sobe)
- [x] `README.md` com instruções de setup testadas manualmente
- [x] Commit inicial do checkpoint de inicialização

## Em Andamento
- (nenhum item ativo no momento — pronto para começar F01)

## Bloqueado / Pendente de Confirmação
- Três suposições de regra de negócio precisam de confirmação do solicitante do desafio antes da implementação das regras de precificação (ver `DECISIONS.md`: D-01, D-02, D-03).

## Próximos Passos
1. Confirmar (ou ajustar) as suposições D-01, D-02 e D-03 com o solicitante do desafio.
2. Começar `FEATURES.md` pela F01 (cadastro de hóspede), uma funcionalidade ativa por vez (WIP=1).
3. Priorizar F06/F07 (cálculo de diária e taxa de estacionamento) com atenção especial aos casos de fronteira sexta→sábado→domingo→segunda, por serem as regras de maior risco.
