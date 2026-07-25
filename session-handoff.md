# Transferência de Sessão

Preencha isto ao final de cada sessão de trabalho. A próxima sessão deve conseguir retomar lendo só este arquivo + `progress.md` + `feature_list.json`.

## Bloqueios
- Três suposições de regra de negócio aguardam confirmação do solicitante do desafio antes de F06/F07/F08/F09 poderem ser implementadas com segurança (ver `DECISIONS.md`: D-01, D-02, D-03). Nenhum outro bloqueio técnico no momento.

## Arquivos Tocados Nesta Sessão
- `AGENTS.md`, `DECISIONS.md`, `progress.md` (renomeado de `PROGRESS.md`), `feature_list.json` (substitui `FEATURES.md`), `session-handoff.md` (novo), `init.sh` (novo)
- `README.md`
- `docker-compose.yml`
- `backend/` (esqueleto Spring Boot completo, sem código de negócio)
- `frontend/` (esqueleto Angular completo, sem código de negócio)

## Próxima Sessão
1. Ler `progress.md` (seção "Próximo Passo Recomendado") e `feature_list.json`.
2. Rodar `./init.sh` para confirmar que o ambiente ainda está saudável.
3. Se as suposições D-01/D-02/D-03 já tiverem sido confirmadas/ajustadas, marcar `F01` como `"status": "active"` em `feature_list.json` e começar a implementação — uma funcionalidade por vez, sem avançar para a próxima antes de `F01` estar `passing` com evidência registrada.
