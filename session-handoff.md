# Transferência de Sessão

Preencha isto ao final de cada sessão de trabalho. A próxima sessão deve conseguir retomar lendo só este arquivo + `progress.md` + `feature_list.json`.

## Bloqueios
- Nenhum. Todas as decisões de negócio pendentes (D-01, D-02, D-03, D-05) foram confirmadas pelo solicitante do desafio nesta sessão. `feature_list.json` já foi revisado para refletir as mudanças. Pronto para começar `F01`.

## Arquivos Tocados Nesta Sessão
- `AGENTS.md` renomeado para `CLAUDE.md` (Claude Code lê `CLAUDE.md`, não `AGENTS.md`); referências atualizadas em `README.md`, `progress.md`, `DECISIONS.md`.
- `DECISIONS.md`: D-01/D-02/D-03/D-05 revisados e confirmados (não são mais suposições); D-11 (reconciliação com o validador de harness do curso) removida; D-12 (entidade `Quarto`) e D-13 (código em inglês, com glossário de domínio) adicionadas.
- `CLAUDE.md`: restrição de check-in (#6), escopo (referência a `Quarto`) e convenções (código em inglês) atualizados para refletir as decisões revisadas.
- `feature_list.json`: `F24` (Cadastro de quarto) e `F25` (Tela de gestão de quartos) adicionados; `F05`/`F06`/`F07`/`F08`/`F15`/`F16` reescritos para refletir `Quarto` em vez de só `CategoriaQuarto`, "sem bloco especial de fim de semana" e cobrança de estacionamento por dia; todos os identificadores em `verification` (classes de teste, arquivos `.spec.ts`) traduzidos para inglês.
- `progress.md` / `session-handoff.md`: bloqueios antigos removidos, menções ao validador de harness do curso removidas.

## Próxima Sessão
1. Ler `progress.md` (seção "Próximo Passo Recomendado") e `feature_list.json`.
2. Rodar `./init.sh` para confirmar que o ambiente ainda está saudável (passou nesta sessão: backend compila/testa, frontend builda e os 3 testes passam).
3. Marcar `F01` como `"status": "active"` em `feature_list.json` e começar a implementação — uma funcionalidade por vez, sem avançar para a próxima antes de `F01` estar `passing` com evidência registrada. Lembrar: código em inglês (D-13), domínio documentado em português.
