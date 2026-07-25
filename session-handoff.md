# Transferência de Sessão

Preencha isto ao final de cada sessão de trabalho. A próxima sessão deve conseguir retomar lendo só este arquivo + `progress.md` + `feature_list.json`.

## Bloqueios
- Nenhum. F01 implementado e passando. `feature_list.json` atualizado com evidência.

## Arquivos Tocados Nesta Sessão
- `backend/src/main/java/.../guest/Guest.java`: entidade JPA (`id`, `name`, `document`, `phone`).
- `backend/src/main/java/.../guest/GuestRepository.java`: `JpaRepository<Guest, Long>`.
- `backend/src/main/java/.../guest/GuestRequest.java` / `GuestResponse.java`: DTOs (records) — request com `@NotBlank` em nome/documento/telefone.
- `backend/src/main/java/.../guest/GuestController.java`: `POST /api/guests`, persiste via `GuestRepository`, retorna 201.
- `backend/src/test/java/.../guest/GuestControllerTest.java`: `@WebMvcTest` + `@MockitoBean` (não `@MockBean` — removido no Spring Boot 4/Spring 7, ver nota abaixo), 4 testes (criação + validação de cada campo obrigatório).
- `backend/src/test/java/.../guest/GuestRepositoryTest.java`: `@DataJpaTest`, 2 testes (persistência + busca por id).
- `feature_list.json`: F01 marcado `passing` com evidência (`./mvnw test -Dtest=GuestControllerTest,GuestRepositoryTest` → 6/6 verdes; suíte completa → 7/7 verdes).
- `progress.md`: atualizado com o que foi concluído e o próximo passo.

## Nota técnica importante para próximas features
Spring Boot 4.1 renomeou pacotes de anotações de teste em relação ao 3.x — confirmado lendo os jars em `~/.m2` (não documentação, que ainda referencia os pacotes antigos):
- `@WebMvcTest` → `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` (não mais `org.springframework.boot.test.autoconfigure.web.servlet`).
- `@DataJpaTest` → `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`.
- `@MockBean` foi **removido**; usar `@MockitoBean` de `org.springframework.test.context.bean.override.mockito.MockitoBean` (de `spring-test`).
- Em testes `@WebMvcTest`, não há bean `ObjectMapper` garantido no contexto fatiado — instancie um `new ObjectMapper()` local no teste em vez de `@Autowired`.

## Próxima Sessão
1. Ler `progress.md` (seção "Próximo Passo Recomendado") e `feature_list.json`.
2. Rodar `./init.sh` para confirmar que o ambiente ainda está saudável.
3. Escolher a próxima funcionalidade `not_started` sem dependências pendentes — **F02** (busca de hóspede, depende só de F01/passing) ou **F03** (cadastro de categoria de quarto, sem dependências) são as candidatas óbvias. Marcar `active` em `feature_list.json`, implementar só ela, rodar `./mvnw test` e registrar evidência antes de considerar `passing`.
4. Lembrar: código em inglês (D-13), domínio documentado em português; usar os pacotes de teste corretos (nota técnica acima) para não perder tempo com imports quebrados.
