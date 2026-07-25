# Lista de Funcionalidades

Fonte única da verdade sobre o que falta fazer. Cada item tem: comportamento esperado, comando de verificação e estado (`not_started` / `active` / `blocked` / `passing`). Ninguém marca um item como `passing` manualmente — só o comando de verificação passando permite a transição. Trabalhar **uma funcionalidade `active` por vez**.

## Backend

| ID | Comportamento | Verificação | Estado |
|----|----|----|----|
| F01 | Cadastrar hóspede (nome, documento, telefone) de forma persistente | `HospedeControllerTest` + `HospedeRepositoryTest` passando | not_started |
| F02 | Buscar hóspede por nome, documento ou telefone | `HospedeControllerTest#buscaPorFiltro` passando | not_started |
| F03 | Cadastrar categoria de quarto (ex.: Standard, Luxo) | `CategoriaQuartoControllerTest` passando | not_started |
| F04 | Configurar preço por dia da semana (seg–dom) para cada categoria | `CategoriaQuartoControllerTest#atualizaPrecos` passando | not_started |
| F05 | Criar reserva (hóspede + categoria + data de entrada/saída previstas) | `ReservaControllerTest#criarReserva` passando | not_started |
| F06 | Calcular valor da(s) diária(s) de uma reserva, aplicando a regra de bloco único do fim de semana (D-02) | `DiariaServiceTest` passando (casos: só dia útil, só fim de semana, atravessando os dois) | not_started |
| F07 | Calcular taxa de estacionamento por diária (dia útil vs. fim de semana; cobrança única no bloco de fim de semana — D-03) | `EstacionamentoServiceTest` passando | not_started |
| F08 | Realizar check-in de uma reserva; emitir alerta (não bloqueante — D-01) se antes das 14h | `ReservaControllerTest#checkinAntesDas14h` + `#checkinValido` passando | not_started |
| F09 | Realizar check-out de uma reserva; cobrar 50% de taxa extra se após 12h; retornar detalhamento do total | `ReservaControllerTest#checkoutComAtraso` + `#checkoutDetalhamento` passando | not_started |
| F10 | Listar hóspedes atualmente no hotel (check-in feito, sem check-out) | `HospedeControllerTest#hospedesNoHotel` passando | not_started |
| F11 | Listar hóspedes com reserva e sem check-in realizado | `HospedeControllerTest#hospedesSemCheckin` passando | not_started |

## Frontend

| ID | Comportamento | Verificação | Estado |
|----|----|----|----|
| F13 | Tela de cadastro e busca de hóspedes | `hospede-form.component.spec.ts` + `hospede-busca.component.spec.ts` passando | not_started |
| F14 | Tela de configuração de preços por categoria/dia da semana | `categoria-preco.component.spec.ts` passando | not_started |
| F15 | Tela de criação de reserva | `reserva-form.component.spec.ts` passando | not_started |
| F16 | Tela de check-in, exibindo alerta quando antes das 14h | `checkin.component.spec.ts` passando | not_started |
| F17 | Tela de check-out, exibindo detalhamento do total antes de confirmar | `checkout.component.spec.ts` passando | not_started |
| F18 | Lista de hóspedes atualmente no hotel | `hospedes-no-hotel.component.spec.ts` passando | not_started |
| F19 | Lista de hóspedes com reserva sem check-in | `hospedes-sem-checkin.component.spec.ts` passando | not_started |

## Infraestrutura & Documentação

| ID | Comportamento | Verificação | Estado |
|----|----|----|----|
| F21 | PostgreSQL local sobe via `docker compose up -d` e a aplicação conecta nele | `docker compose up -d && ./mvnw test` passando | not_started |
| F22 | `README.md` documenta setup completo (backend, frontend, banco) do zero | Uma sessão nova consegue rodar o projeto seguindo só o README | not_started |
| F23 | Repositório publicado em Git público, link pronto para envio | Link acessível sem autenticação | not_started |

## Notas de Granularidade
- F06 e F07 são as funcionalidades de maior risco (mais regras de negócio implícitas) — devem ganhar mais casos de teste do que as demais, cobrindo especialmente a fronteira sexta→sábado→domingo→segunda.
- F08/F09 dependem de F06/F07 já estarem `passing`.
- Itens do frontend (F13–F19) dependem dos endpoints correspondentes do backend já estarem `passing`.
