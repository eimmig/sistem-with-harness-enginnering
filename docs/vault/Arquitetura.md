---
tags: [arquitetura]
---

# Arquitetura

## Stack

| Camada | Tecnologia |
|---|---|
| Backend | Java 17, Spring Boot 4.1.x, Maven, Spring Data JPA, Bean Validation, JUnit 5 + Mockito |
| Documentação da API | springdoc-openapi (Swagger UI) — `/swagger-ui.html`, `/v3/api-docs` |
| Frontend | Angular 19, Angular Material, RxJS, Karma + Jasmine |
| Banco | PostgreSQL (runtime, via Docker Compose) / H2 (testes) |

Monorepo único (`backend/` + `frontend/`) — decisão D-07.

## Camadas do backend
Controller → Service → Repository, padrão Spring Data JPA. Testes de controller usam H2 em memória (`./mvnw test` não depende do Docker); PostgreSQL real só é necessário para `spring-boot:run`.

## Decisões de design mais relevantes
(lista completa em `DECISIONS.md`; aqui só as que moldam o desenho dos módulos)

- **D-01** — Check-in antes das 14h exige confirmação explícita do atendente; quarto precisa estar `DISPONIVEL` independentemente do horário. Ver [[Check-in e Check-out]].
- **D-02 — Diária de fim de semana** — não é um bloco especial de cálculo; cada diária (segunda a domingo) é calculada da mesma forma, só o valor muda por dia/categoria. O total é o somatório das diárias individuais. Ver [[Diária]].
- **D-03** — Taxa de estacionamento cobrada por dia de estadia (consequência de D-02). Ver [[Taxa de Estacionamento]].
- **D-05** — [[Reserva]] referencia um [[Quarto]] específico, não apenas uma [[Categoria de Quarto]].
- **D-09** — Postgres do Docker exposto em `5433` (não `5432`), para não colidir com um Postgres nativo já instalado na máquina de desenvolvimento.
- **D-12** — [[Quarto]] é entidade própria (número, categoria, status: `DISPONIVEL`/`SUJO`/`OCUPADO`).
- **D-13** — código-fonte em inglês; ver [[Glossário de Domínio]].
- **D-15** — busca de hóspede (`GET /api/guests`) usa filtros opcionais `name`/`document`/`phone` combinados por AND, partial match case-insensitive, via `Specification`/`JpaSpecificationExecutor` do Spring Data JPA. Ver [[Hóspede]].
- **D-16** — preço por dia da semana da [[Categoria de Quarto]] é um `Map<DayOfWeek, BigDecimal>` embutido (`@ElementCollection`) em `RoomCategory`, não uma entidade própria; atualização via `PUT` exige as 7 diárias completas (sem update parcial de um único dia).
- **D-17** — [[Quarto]]: `number` é `String` (rótulo, não quantidade); `PATCH /api/rooms/{id}/status` já faz parte de F24 (backend), diferente de F03/F04 que separaram cadastro e configuração; quarto novo nasce `AVAILABLE`.
- **D-18** — [[Reserva]]: criação não valida `status` do quarto (regra #6 é sobre check-in, não sobre reserva); só valida existência de hóspede/quarto e que check-out é depois do check-in. Campo para "hóspede tem carro" (regra #5) fica pendente para F07.
- **D-19** — [[Diária]]: `DailyRateService.calculate(RoomCategory, checkIn, checkOut)` é serviço puro (sem endpoint); noite atribuída ao dia da semana do seu check-in; implementa D-02 diretamente (soma de diárias individuais, sem bloco especial de fim de semana).
- **D-20** — [[Reserva]]/[[Taxa de Estacionamento]]: `Reservation.parkingRequested` guarda "hóspede tem carro e usa vaga"; `ParkingFeeService` classifica segunda-sexta como dia útil (R$15) e sábado-domingo como fim de semana (R$20) — classificação fixa, independente do preço por dia configurável de F04.
- **D-21** — [[Check-in e Check-out]]: `ReservationController` usa `Clock` injetável (bean `ClockConfig`) para "agora" testável; `Reservation.actualCheckIn` novo; quarto indisponível ou reserva já com check-in → `409 CONFLICT`; check-in antes das 14h sem confirmação → `400 BAD_REQUEST`.
- **D-22** — [[Check-in e Check-out]]: check-out é chamada única (sem prévia separada); `Reservation.actualCheckOut` novo; taxa de atraso usa o preço da última diária hospedada (`actualCheckOut.minusDays(1)`'s day-of-week); quarto vai para `DIRTY`, não `AVAILABLE`.
- **D-23** — Infraestrutura do frontend: chamadas à API via caminho relativo `/api/...` + `proxy.conf.json` (evita CORS em dev); `provideHttpClient()`/`provideAnimationsAsync()` em `app.config.ts`; `@angular/animations` adicionado como dependência; componentes standalone com rotas lazy (`loadComponent`); estrutura por feature em `frontend/src/app/features/<domínio>/`; `data-testid` em elementos interativos; texto de UI em português, código em inglês (extensão de D-13).
- **D-24** — [[Categoria de Quarto]]: `GET /api/room-categories` adicionado ao backend como parte do escopo de F14 (tela precisa listar categorias existentes; F03 só tinha `POST`) — não é scope creep, é o que a própria feature de tela exige.
- **D-25** — [[Quarto]]: `GET /api/rooms` adicionado como parte do escopo de F25, mesmo padrão de D-24; troca de status na tela é inline (sem confirmação separada), diferente de check-in/check-out que têm regras de negócio mais pesadas.
- **D-26** — [[Reserva]] (F15): busca de hóspede por nome via `GuestService.search()` (sem autocomplete); datas via `<input type="datetime-local">` (sem date-picker dedicado); seletor de quarto mostra todos os quartos, sem filtrar por status (consistente com D-18).
- **D-27** — [[Check-in e Check-out]] (F16): `GET /api/reservations/pending-check-in` expõe query já existente; aviso das 14h é inline na linha da tabela (sem modal) — `400` do backend dispara o aviso, `409` dispara mensagem de erro.
- **D-28** — [[Check-in e Check-out]] (F17): `GET /api/reservations/pending-check-out` expõe query já existente; detalhamento (regra #8) exibido como resultado pós-ação, não prévia separada — consistente com D-22 (check-out é chamada única).
- **D-29** — F26-F33: testes E2E cobrindo API (Testcontainers Postgres + `TestRestTemplate`, pacote `e2e`, classes `<Domínio>E2ETest`) e UI (Playwright), um par de features por domínio de controller.
- **D-30** — F34-F37: refatoração visual do frontend por domínio (ngx-mask para CPF/telefone/moeda, `MatDatepicker` no lugar de `datetime-local`), rodando antes do E2E de UI correspondente.
- **D-32** — [[Hóspede]] (F26): `pom.xml` do backend ganhou `spring-boot-testcontainers`, `spring-boot-resttestclient` (+ `spring-boot-restclient`, dependência transitiva não puxada automaticamente — precisou ser declarada explicitamente) e `org.testcontainers:testcontainers`/`testcontainers-junit-jupiter`/`testcontainers-postgresql` (nomes de artefato do Testcontainers 2.x, diferentes da convenção 1.x usada em tutoriais antigos). `maven-surefire-plugin` exclui `**/e2e/**/*E2ETest.java` do `./mvnw test` padrão (por isso `init.sh` continua sem depender de Docker); a verificação de cada feature E2E roda a classe explicitamente via `-Dtest=<Domínio>E2ETest`, o que sobrepõe o exclude.
- **D-33** — [[Reserva]]/[[Check-in e Check-out]] (F29): `ReservationE2ETest` precisa controlar "agora" para testar check-in antes/depois das 14h e check-out antes/depois das 12h de forma determinística, sem MockMvc/mocks. Solução: `@TestConfiguration` aninhada com um `Clock` mutável de teste (`@Primary`), que colide em nome de bean (`clock`) com `ClockConfig` de produção — Spring por padrão proíbe esse override, então o teste liga `spring.main.allow-bean-definition-overriding=true` via `@SpringBootTest(properties = ...)`, escopado só a essa classe de teste (não afeta produção nem os outros `*E2ETest`). Mesmo seam de D-21 (Clock injetável para testabilidade), aplicado a um contexto Spring real em vez de um bean mockado.
- **D-34** — [[Hóspede]] (F34): `ngx-mask` mais recente (22.x) exige Angular 22 (peer dependency); instalada a versão `19.0.7`, cujo peer range (`>=14.0.0`) é compatível com o Angular 19 do projeto. Layout compartilhado (D-30) implementado como classes globais em `styles.scss` em vez de componentes Angular dedicados (ex.: um `PageHeaderComponent`) — decisão de escopo mínimo, já que o conjunto de páginas é pequeno e homogêneo; reavaliar se o número de páginas crescer o suficiente para justificar a abstração. Specs de componente que renderizam um campo com `mask` (direto ou via componente filho) precisam de `provideEnvironmentNgxMask()` no `TestBed` -- sem isso, a injeção do token de configuração do ngx-mask falha (`NullInjectorError`).

## Frontend
Stack: Angular 19 standalone (sem NgModules), Angular Material (tema `azure-blue` prebuilt), rotas lazy via `loadComponent`. Estrutura por feature em `frontend/src/app/features/<domínio>/` — cada uma com `*.service.ts` (chamadas HTTP), `*.model.ts` (interfaces espelhando os DTOs do backend) e componentes standalone (um por responsabilidade, ex.: formulário e busca separados, compostos numa página que registra a rota). Ver D-23.

## Documentação da API
Adicionado `springdoc-openapi-starter-webmvc-ui` (versão 3.0.3, compatível com Spring Boot 4 / Spring Framework 7) como infraestrutura — não é uma funcionalidade rastreada em `feature_list.json`. Configuração em `backend/src/main/java/com/projetosenior/gestaohospedes/config/OpenApiConfig.java`. A documentação se preenche automaticamente conforme os controllers de cada feature forem implementados; atualmente cobre `POST`/`GET /api/guests` (+ `/in-hotel`, `/without-check-in`, F01/F02/F10/F11), `POST`/`GET /api/room-categories` + `PUT /api/room-categories/{id}/prices` (F03/F04/F14), `POST`/`GET /api/rooms` + `PATCH /api/rooms/{id}/status` (F24/F25) e `POST /api/reservations` + `POST /api/reservations/{id}/check-in` + `POST /api/reservations/{id}/check-out` (F05/F07/F08/F09). F06/F07 (`DailyRateService`/`ParkingFeeService`) continuam sem endpoint HTTP próprio — são consumidos internamente pelo checkout.

Ver também: [[Visão Geral do Sistema]], [[Mapa de Funcionalidades]].
