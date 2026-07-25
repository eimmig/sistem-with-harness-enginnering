# Transferência de Sessão

Preencha isto ao final de cada sessão de trabalho. A próxima sessão deve conseguir retomar lendo só este arquivo + `progress.md` + `feature_list.json`.

## Bloqueios
- Nenhum. F02 implementado e passando. `feature_list.json` atualizado com evidência.

## Arquivos Tocados Nesta Sessão
- `backend/src/main/java/.../guest/GuestRepository.java`: passou a estender também `JpaSpecificationExecutor<Guest>`.
- `backend/src/main/java/.../guest/GuestSpecifications.java` (novo): `Specification<Guest>` estáticas `nameContains`/`documentContains`/`phoneContains` (LIKE parcial, case-insensitive).
- `backend/src/main/java/.../guest/GuestController.java`: novo `GET /api/guests` com query params opcionais `name`/`document`/`phone`, combinados com AND via `Specification`; sem filtros retorna todos os hóspedes.
- `backend/src/test/java/.../guest/GuestControllerTest.java`: +3 testes (`searchByFilter`, `searchWithoutFiltersReturnsAllGuests`, `searchWithNoMatchesReturnsEmptyList`).
- `backend/src/test/java/.../guest/GuestRepositoryTest.java`: +3 testes (partial/case-insensitive por nome, combinação AND nome+documento, sem resultados).
- `DECISIONS.md`: D-15 registrada (design da busca — filtros combinados por AND, partial match, sem endpoint dedicado `/search`).
- `feature_list.json`: F02 marcado `passing` com evidência (`./mvnw test -Dtest=GuestControllerTest,GuestRepositoryTest` → 12/12 verdes; `./init.sh` completo também passando).
- `progress.md`: atualizado com o que foi concluído e o próximo passo.

## Nota técnica importante para próximas features
- Em Spring Data JPA 4.x (usado pelo Spring Boot 4.1), `Specification.where(null)` foi removido — usar `Specification.unrestricted()` como especificação neutra de partida ao compor filtros opcionais dinamicamente.
- Notas da sessão anterior sobre pacotes de teste do Spring Boot 4.1 continuam válidas: `@WebMvcTest` → `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`; `@DataJpaTest` → `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`; `@MockBean` removido, usar `@MockitoBean` (`org.springframework.test.context.bean.override.mockito.MockitoBean`); instanciar `ObjectMapper` local em testes `@WebMvcTest`.

## Próxima Sessão
1. Ler `progress.md` (seção "Próximo Passo Recomendado") e `feature_list.json`.
2. Rodar `./init.sh` para confirmar que o ambiente ainda está saudável.
3. Escolher a próxima funcionalidade `not_started` sem dependências pendentes — **F03** (cadastro de categoria de quarto, sem dependências) é a candidata óbvia; abre caminho para F04 (preço por dia da semana) e F24 (cadastro de quarto). Marcar `active` em `feature_list.json`, implementar só ela, rodar `./mvnw test` e registrar evidência antes de considerar `passing`.
4. Lembrar: código em inglês (D-13), domínio documentado em português; usar os pacotes de teste corretos (nota técnica acima) para não perder tempo com imports quebrados.
