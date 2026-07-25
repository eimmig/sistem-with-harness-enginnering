# Transferência de Sessão

Preencha isto ao final de cada sessão de trabalho. A próxima sessão deve conseguir retomar lendo só este arquivo + `progress.md` + `feature_list.json`.

## Bloqueios
- **Nenhum bloqueio de código.** F19 implementado e passando — **todo o backlog de implementação (F01–F19, F21, F22, F24, F25) está `passing`.**
- **F23 (repositório Git público) é a única funcionalidade restante.** Não é uma tarefa de código: precisa que o usuário decida onde publicar (GitHub, GitLab, conta/organização), e autorize explicitamente o `git remote add` + `git push` para um remoto público — não fazer isso sem confirmação explícita, por ser uma ação visível/pública irreversível de baixo custo de espera.

## Arquivos Tocados Nesta Sessão
- `frontend/src/app/features/guest/guest.service.ts`: +`guestsWithoutCheckIn()`.
- `frontend/src/app/features/guest/guests-without-checkin/` (novo): `GuestsWithoutCheckinComponent` + spec (2 testes) — mesmo padrão de `GuestsInHotelComponent` (F18), tabela simples sem formulário.
- `frontend/src/app/app.routes.ts`: rota `/guests-without-check-in` (lazy).
- `frontend/src/app/app.component.html`: novo link de navegação "Sem Check-in".
- `feature_list.json`: F19 marcado `passing` com evidência (51/51 testes Karma + `ng build` + `./init.sh` completo, exit 0). Nenhuma mudança no backend foi necessária.
- `progress.md`: reescrito para refletir que só resta F23; lista de "Concluído" e "Bloqueado" atualizadas.
- `docs/vault/Hóspede.md`, `docs/vault/Mapa de Funcionalidades.md` (+ nova seção "Status geral"): atualizados.

## Nota técnica importante para próximas features
- Nenhuma nova. O backlog de código está completo — não há mais features com decisões de design pendentes.

## Próxima Sessão
1. Ler `progress.md` e `feature_list.json` — confirmar que só F23 está `not_started`.
2. Rodar `./init.sh` para confirmar que o ambiente ainda está saudável (deve passar sem alterações desde a última sessão).
3. **F23 (repositório Git público)** é a única pendência. Antes de qualquer ação:
   - Perguntar ao usuário em qual host/conta/organização o repositório deve ser publicado, e o nome desejado.
   - Confirmar explicitamente antes de rodar `git remote add` + `git push` — é uma ação visível publicamente e não deve ser feita autonomamente sem autorização clara.
   - Depois de publicado, marcar F23 como `passing` em `feature_list.json` com o link como evidência, atualizar `docs/vault/Mapa de Funcionalidades.md`, e comitar.
4. Depois de F23, o projeto inteiro (todas as 24 funcionalidades) estará `passing` — pronto para entrega final. Vale revisar o `README.md` uma última vez para garantir que as instruções de setup continuam precisas antes do envio.
