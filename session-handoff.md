# Transferência de Sessão

Preencha isto ao final de cada sessão de trabalho. A próxima sessão deve conseguir retomar lendo só este arquivo + `progress.md` + `feature_list.json`.

## Bloqueios
- Nenhum. F10 implementado e passando. `feature_list.json` atualizado com evidência.

## Arquivos Tocados Nesta Sessão
- `backend/src/main/java/.../reservation/ReservationRepository.java`: `findByActualCheckInIsNotNullAndActualCheckOutIsNull()` (usado agora) e `findByActualCheckInIsNull()` (declarado já, será usado por F11).
- `backend/src/main/java/.../guest/GuestController.java`: passou a injetar `ReservationRepository`; novo `GET /api/guests/in-hotel`.
- `backend/src/test/java/.../guest/GuestControllerTest.java`: `@MockitoBean ReservationRepository`; +2 testes (`guestsInHotel`, `guestsInHotelReturnsEmptyListWhenNoOneIsCheckedIn`).
- `feature_list.json`: F10 marcado `passing` com evidência (`./mvnw test -Dtest=GuestControllerTest,GuestRepositoryTest` → 14/14 verdes; `./init.sh` completo também passando).
- `progress.md`, `docs/vault/Hóspede.md`, `docs/vault/Mapa de Funcionalidades.md`: atualizados.

## Nota técnica importante para próximas features
- Nenhuma nova além das já registradas (destaque: `@WebMvcTest` não carrega `@Service`/`@Component` — precisa `@MockitoBean` para qualquer serviço injetado no controller sob teste).

## Próxima Sessão
1. Ler `progress.md` (seção "Próximo Passo Recomendado") e `feature_list.json`.
2. Rodar `./init.sh` para confirmar que o ambiente ainda está saudável.
3. Próxima funcionalidade: **F11** (listagem de hóspedes sem check-in) — depende de F05, `passing`. `ReservationRepository.findByActualCheckInIsNull()` já existe; só falta `GuestController#guestsWithoutCheckIn` (`GET /api/guests/without-check-in`) + teste `GuestControllerTest#guestsWithoutCheckIn` (nome exigido pela verificação em `feature_list.json`).
4. Depois de F11, o núcleo de negócio do backend (F01–F11, F24) estará completo. Restam: telas do frontend (F13–F19, F25) e F23 (repositório Git público) — nenhuma dependência bloqueante entre elas além das já satisfeitas.
5. Atualizar `docs/vault/` antes do commit — parte obrigatória da Definição de Pronto (CLAUDE.md).
