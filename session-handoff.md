# Transferência de Sessão

Preencha isto ao final de cada sessão de trabalho. A próxima sessão deve conseguir retomar lendo só este arquivo + `progress.md` + `feature_list.json`.

## Bloqueios
- Nenhum. F18 implementado e passando. `feature_list.json` atualizado com evidência.
- F23 (repositório Git público) segue como a única pendência que não pode ser resolvida por código — será o único item restante depois de F19.

## Arquivos Tocados Nesta Sessão
- `frontend/src/app/features/guest/guest.service.ts`: +`guestsInHotel()`.
- `frontend/src/app/features/guest/guests-in-hotel/` (novo): `GuestsInHotelComponent` + spec (2 testes) — tabela simples, sem formulário, consome `GET /api/guests/in-hotel`.
- `frontend/src/app/app.routes.ts`: rota `/guests-in-hotel` (lazy).
- `frontend/src/app/app.component.html`: novo link de navegação "No Hotel".
- `feature_list.json`: F18 marcado `passing` com evidência (49/49 testes Karma + `ng build` + `./init.sh` completo, exit 0). Nenhuma mudança no backend foi necessária.
- `progress.md`, `docs/vault/Hóspede.md`, `docs/vault/Mapa de Funcionalidades.md`: atualizados.

## Nota técnica importante para próximas features
- Nenhuma nova. F18 confirma que quando o endpoint já existe (caso de F10/F11), a tela de listagem é só um componente simples sem decisão de design nova — não precisou de entrada em `DECISIONS.md`.

## Próxima Sessão
1. Ler `progress.md` (seção "Próximo Passo Recomendado") e `feature_list.json`.
2. Rodar `./init.sh` para confirmar que o ambiente ainda está saudável.
3. Próxima funcionalidade: **F19** (lista de hóspedes sem check-in) — depende de F11, `passing`. Mesmo padrão de F18: `GuestService.guestsWithoutCheckIn()` (novo método) consumindo `GET /api/guests/without-check-in` (já existente desde F11), `GuestsWithoutCheckInComponent`, rota `/guests-without-check-in`, link de navegação. Verificação: `guests-without-checkin.component.spec.ts`.
4. **Depois de F19, todo o backlog de código estará `passing`** (F01–F19, F21–F22, F24–F25). Resta só **F23** (repositório Git público) — não é uma tarefa de código, é uma decisão/ação do usuário (criar/publicar o repositório, decidir conta/organização, visibilidade). Nesse ponto, a sessão deve reportar ao usuário que o backlog de implementação está completo e que F23 aguarda ação humana — não tentar resolvê-la sozinha.
5. Atualizar `docs/vault/` antes do commit — parte obrigatória da Definição de Pronto (CLAUDE.md).
