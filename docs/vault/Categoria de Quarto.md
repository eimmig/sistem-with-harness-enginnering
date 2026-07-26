---
tags: [módulo]
---

# Categoria de Quarto (`RoomCategory`)

## Responsabilidade
Agrupar [[Quarto|quartos]] (ex.: Standard, Luxo) e ser a dona do preço configurável da [[Diária]] por dia da semana.

## Regras de negócio relevantes
- Diária de dia útil (segunda a sexta) tem valor configurável por categoria (regra #2).
- O valor da diária é configurável por categoria **e por dia da semana**, via tela de configuração — não é constante fixa no código (regra #4).
- A estadia de fim de semana não é um bloco especial de cálculo (D-02 em [[Arquitetura]]) — cada dia individual tem seu próprio preço.

## Funcionalidades relacionadas
[[Mapa de Funcionalidades]]: F03 (cadastro), F04 (configuração de preço por dia da semana), F14 (tela de configuração de preços), F27 (E2E de API), F35 (refatoração visual), F31 (E2E de UI).

## Relações
Cada [[Quarto]] pertence a uma categoria. A categoria é a fonte do valor usado no cálculo de [[Diária]].

## Status atual
- **F03 (cadastro) — implementado e `passing`.** `RoomCategory` (entidade: `id`, `name`), `RoomCategoryRepository`, `RoomCategoryController` (`POST /api/room-categories`) em `backend/src/main/java/.../roomcategory/`. Validação `@NotBlank` em nome.
- **F04 (preço por dia da semana) — implementado e `passing`.** `RoomCategory.prices` (`Map<DayOfWeek, BigDecimal>`, `@ElementCollection` em tabela auxiliar `room_category_price`), `RoomCategoryController#updatePrices` (`PUT /api/room-categories/{id}/prices`). Atualização exige as 7 chaves de `DayOfWeek` com valor positivo — não há update parcial de um único dia. Decisão de design em D-16 (ver [[Arquitetura]]).
- **`GET /api/room-categories`** (listagem) foi adicionado em F14, quando a tela de configuração de preços precisou listar categorias existentes (D-24) — F03 originalmente só tinha `POST`.
- **F14 (tela de configuração de preços) — implementada e `passing`.** `RoomCategoryFormComponent` (cadastro) + `RoomCategoryPriceComponent` (seleciona categoria, configura os 7 dias), compostos em `RoomCategoriesPageComponent` (rota `/room-categories`), em `frontend/src/app/features/room-category/`.
- **F27 (E2E de API) — implementado e `passing`.** `RoomCategoryE2ETest` em `backend/src/test/java/.../e2e/`, mesma infra Testcontainers/`TestRestTemplate` de F26 (ver D-29/D-32 em [[Arquitetura]]). Cobre cadastro, listagem, atualização de preço (7 dias, persistido e refletido em novo `GET`) e as rejeições de `RoomCategoryController#updatePrices` (semana incompleta → 400, categoria inexistente → 404).
- **F35 (refatoração visual) — implementado e `passing`.** Reaproveita o layout/tema/`ngx-mask` estabelecidos em F34. Os 7 campos de preço em `RoomCategoryPriceComponent` ganham máscara de moeda (`mask="separator.2"`, `thousandSeparator="."`, `decimalMarker=","`, `prefix="R$ "`), com `outputTransformFn`/`inputTransformFn` convertendo entre o número puro armazenado no `FormControl<number>` e a string mascarada exibida — o payload HTTP continua indo como número (`120`, não `"120,00"`), preservando o contrato com `RoomCategoryPricesRequest` do backend. Nota de UX (D-34): a máscara não desloca decimais estilo calculadora — o atendente digita a vírgula explicitamente (ex.: "120,00"), como ao escrever o valor por extenso. `RoomCategoriesPageComponent` passa a usar `mat-card` para agrupar as duas seções.
- **F31 (E2E de UI) — implementado e `passing`.** `frontend/e2e/room-category-flow.e2e.spec.ts`: cadastra categoria pela tela, seleciona no `mat-select` (atualizado via `refreshSignal`), preenche os 7 preços com vírgula decimal explícita (`120,00`/`150,00`), submete e confere mensagem de sucesso, depois recarrega a página e reseleciona a categoria confirmando que os preços persistidos reaparecem (asserção tolerante a redisplay sem forçar 2 casas decimais). Observações técnicas sobre a interação com `mat-select`/máscara de moeda em testes automatizados registradas no addendum de D-38 (ver [[Arquitetura]]).
