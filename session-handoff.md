# Transferência de Sessão

Preencha isto ao final de cada sessão de trabalho. A próxima sessão deve conseguir retomar lendo só este arquivo + `progress.md` + `feature_list.json`.

## Bloqueios
- Nenhum bloqueio de código. F11 implementado e passando — **todo o backend de negócio (F01–F11, F24) está `passing`**.
- F23 (repositório Git público) é a única pendência que não pode ser resolvida por implementação de código — precisa de ação do usuário (criar/publicar o repositório, decidir a conta/organização).

## Arquivos Tocados Nesta Sessão
- `backend/src/main/java/.../guest/GuestController.java`: novo `GET /api/guests/without-check-in`.
- `backend/src/test/java/.../guest/GuestControllerTest.java`: +2 testes (`guestsWithoutCheckIn`, `guestsWithoutCheckInReturnsEmptyListWhenEveryoneHasCheckedIn`).
- `feature_list.json`: F11 marcado `passing` com evidência (`./mvnw test -Dtest=GuestControllerTest,GuestRepositoryTest` → 16/16 verdes; `./init.sh` completo também passando).
- `progress.md`: reescrito de forma mais concisa (a lista de features concluídas estava duplicando o campo `evidence` de `feature_list.json` — agora só resume, com os detalhes vivendo em `feature_list.json` e nos commits).
- `docs/vault/Hóspede.md`, `docs/vault/Mapa de Funcionalidades.md`: atualizados.

## Nota técnica importante para próximas features
- Nenhuma nova além das já registradas nas sessões anteriores.

## Próxima Sessão
1. Ler `progress.md` (seção "Próximo Passo Recomendado") e `feature_list.json`.
2. Rodar `./init.sh` para confirmar que o ambiente ainda está saudável.
3. Todo o backend de negócio está `passing`. Restam as telas do frontend Angular: **F13** (cadastro/busca de hóspedes, depende de F01/F02) é a próxima candidata natural — sem dependências pendentes. Depois: F14, F25, F15 (depende de F25), F16, F17, F18, F19.
4. Antes de começar F13, explorar a estrutura atual de `frontend/src/app/` (o esqueleto Angular 19 + Material foi criado na inicialização do projeto, mas nenhuma tela de negócio foi implementada ainda) para entender convenções de módulos/rotas já estabelecidas, se houver.
5. F23 (repositório Git público) não é uma tarefa de código — é a única funcionalidade que deve ficar como bloqueio pendente de decisão do usuário quando todo o resto estiver `passing`.
6. Atualizar `docs/vault/` antes do commit — parte obrigatória da Definição de Pronto (CLAUDE.md).
