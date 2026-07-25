# Decisões de Design

## [D-01] 2026-07-25 (revisado 2026-07-25) — Check-in antes das 14h exige confirmação do atendente; quarto precisa estar disponível
- **Motivo**: o sistema deve exibir um aviso quando o check-in for solicitado antes das 14h e perguntar explicitamente ao atendente se deseja prosseguir mesmo assim (confirmação ativa, não apenas um aviso informativo). Além disso, o check-in só pode ser efetuado se o quarto estiver com status `DISPONIVEL` (ver D-12) — independentemente do horário, quarto `SUJO` ou `OCUPADO` bloqueia o check-in.
- **Alternativa descartada**: bloquear completamente o check-in antes das 14h, sem opção de override.
- **Status**: decisão confirmada pelo solicitante do desafio.

## [D-02] 2026-07-25 (revisado 2026-07-25) — Diária de fim de semana usa o mesmo cálculo da diária de dia útil; só o valor muda
- **Motivo**: não há tratamento especial para o fim de semana como bloco. Cada diária (segunda a domingo) é calculada da mesma forma; a única diferença é o valor, configurável por categoria e por dia da semana na tela de configuração (ver restrição #4). O total da estadia é o somatório das diárias individuais de cada dia.
- **Alternativa descartada**: tratar o fim de semana como uma diária única combinada (sexta a segunda) com regra própria — rejeitada porque o fim de semana não é um caso especial de cálculo, apenas dias com preço diferente.
- **Status**: decisão confirmada pelo solicitante do desafio.

## [D-03] 2026-07-25 (revisado 2026-07-25) — Taxa de estacionamento é cobrada por dia
- **Motivo**: consequência de D-02 — como não existe mais bloco de fim de semana, a taxa de estacionamento é cobrada uma vez por dia de estadia, usando o valor correspondente ao dia (dia útil ou fim de semana, conforme restrição #5).
- **Status**: decisão confirmada pelo solicitante do desafio.

## [D-04] 2026-07-25 — Java 17 como versão alvo (compatível com "Java 11 ou superior")
- **Motivo**: Spring Boot 3.x (linha atual estável) exige Java 17+. A JDK 25 está instalada localmente; o `pom.xml` fixa `<java.version>17</java.version>` para manter compatibilidade ampla com ambientes de avaliação que talvez não tenham a JDK mais recente, mesmo compilando localmente com uma JDK mais nova.

## [D-05] 2026-07-25 (revisado 2026-07-25) — Reserva referencia um Quarto específico, não uma CategoriaQuarto
- **Motivo**: a reserva é alocada em um quarto físico específico, não apenas em uma categoria. Cada quarto tem vínculo com uma `CategoriaQuarto` (ver D-12), e é dessa categoria que vem o valor da diária.
- **Alternativa descartada**: reserva referenciando só a categoria, sem quarto físico — decisão original de escopo, revertida porque o solicitante confirmou que o controle de quartos individuais faz parte do escopo.
- **Status**: decisão confirmada pelo solicitante do desafio.

## [D-06] 2026-07-25 — Testes de frontend com Karma + Jasmine (padrão do Angular CLI)
- **Motivo**: zero configuração adicional — já vem pronto com `ng new`, e o Chrome está instalado localmente para rodar os testes em modo headless. A alternativa (Jest) reduziria a dependência de navegador, mas exigiria configuração extra não pedida pelo desafio.

## [D-07] 2026-07-25 — Monorepo único (backend/ + frontend/) em vez de dois repositórios
- **Motivo**: a especificação pede "criar repositório em um repositório GIT" (singular) e um único link para avaliação. Um monorepo simplifica isso sem ferir nenhum requisito técnico.

## [D-09] 2026-07-25 — PostgreSQL do Docker exposto na porta 5433, não 5432
- **Motivo**: durante a validação da inicialização, descobrimos um PostgreSQL nativo já instalado e escutando na porta 5432 desta máquina, disputando a porta com o container Docker. O backend acabava conectando no Postgres nativo (sem o banco `gestao_hospedes`) em vez do container, causando `FATAL: banco de dados "gestao_hospedes" não existe`. Ambientes de desenvolvedores/avaliadores frequentemente têm um Postgres local instalado, então evitar a porta padrão é mais robusto. `docker-compose.yml` mapeia `5433:5432`; `application.properties` aponta para `localhost:5433`.
- **Como foi descoberto**: `Get-NetTCPConnection -LocalPort 5432` mostrou dois processos escutando na mesma porta — o proxy do Docker Desktop e um `postgres.exe` nativo.

## [D-10] 2026-07-25 — Credenciais do banco parametrizadas via variável de ambiente
- **Motivo**: o linter do editor sinalizou a senha fixa `postgres` em `application.properties` como segredo hardcoded. Trocado para `${DB_USER:postgres}` / `${DB_PASSWORD:postgres}` (Spring) e `${DB_USER:-postgres}` / `${DB_PASSWORD:-postgres}` (docker-compose) — mantém o valor padrão funcionando sem configuração extra localmente, mas permite sobrescrever via variável de ambiente em outros ambientes, sem precisar versionar nenhuma credencial real.

## [D-08] 2026-07-25 — Spring Boot 4.1.0 (versão atual do Spring Initializr) e H2 para testes
- **Motivo**: o Spring Initializr gerou o projeto na versão estável atual, Spring Boot 4.1.0 (compatível com "Java 11 ou superior" e mais recente que a linha 3.x mencionada inicialmente no `CLAUDE.md` — corrigido lá).
- **Testes**: `src/main/resources/application.properties` aponta para o PostgreSQL do `docker-compose.yml`; `src/test/resources/application.properties` aponta para H2 em memória. Isso permite que `./mvnw test` funcione sem depender do Docker estar rodando — o Postgres real só é necessário para `spring-boot:run`. Testes de integração mais realistas com Postgres via Testcontainers ficam como evolução futura, não implementada nesta fase de inicialização (evitar overengineering do esqueleto).

## [D-12] 2026-07-25 — Quarto como entidade própria (numero, categoria, status)
- **Motivo**: consequência de D-05 — como a reserva agora referencia um quarto específico, o quarto precisa existir como entidade própria, com número, vínculo com `CategoriaQuarto` (de onde vem o valor da diária) e status.
- **Status possíveis do quarto**: `DISPONIVEL`, `SUJO`, `OCUPADO`.
- **Status**: decisão confirmada pelo solicitante do desafio.

## [D-13] 2026-07-25 — Código-fonte (identificadores, mensagens de erro) em inglês
- **Motivo**: solicitação explícita do solicitante do desafio — apesar do domínio do negócio ser descrito em português na documentação (este arquivo, `CLAUDE.md`, `feature_list.json`), o código-fonte (classes, variáveis, constantes, mensagens de erro/validação, nomes de arquivo) deve ser escrito em inglês. Vale também para nomes de classes de teste e arquivos `.spec.ts` referenciados no campo `verification` de `feature_list.json`.
- **Impacto**: substitui a convenção anterior de nomear o domínio em português (`Hospede`, `Reserva`, `CategoriaQuarto`) — convenção atualizada em `CLAUDE.md`.
- **Glossário de domínio** (para manter consistência entre sessões):
  | Português | Inglês |
  |---|---|
  | Hóspede | `Guest` |
  | Reserva | `Reservation` |
  | Categoria de Quarto / `CategoriaQuarto` | `RoomCategory` |
  | Quarto | `Room` |
  | Diária | `DailyRate` |
  | Taxa de estacionamento | `ParkingFee` |
  | Check-in / Check-out | `CheckIn` / `CheckOut` (já em inglês) |
- **Status**: decisão confirmada pelo solicitante do desafio.

## [D-16] 2026-07-25 — Preço por dia da semana: `@ElementCollection` embutida em `RoomCategory`, atualização exige as 7 diárias completas
- **Motivo**: a restrição #4 pede preço configurável por categoria e por dia da semana, via tela de configuração. Em vez de uma entidade própria (`RoomCategoryPrice`) com repositório dedicado, optou-se por um `Map<DayOfWeek, BigDecimal>` como `@ElementCollection` da própria `RoomCategory` (tabela auxiliar `room_category_price`, chave composta categoria+dia) — mais simples, já que os preços não têm identidade ou ciclo de vida próprios fora da categoria. O endpoint `PUT /api/room-categories/{id}/prices` exige que as 7 chaves de `DayOfWeek` estejam presentes e com valor positivo a cada atualização (não há update parcial de um único dia) — evita o estado inconsistente de uma categoria com diária sem preço configurado, o que quebraria o cálculo de diária (F06).
- **Alternativa descartada**: entidade `RoomCategoryPrice` separada com repositório próprio — mais alinhada a "uma tabela por conceito", mas overengineering para um dado que não é referenciado individualmente por nenhuma outra entidade.
- **Status**: suposição registrada; ajustável se o solicitante do desafio pedir update parcial (um dia por vez).

## [D-15] 2026-07-25 — Busca de hóspede: `GET /api/guests` com filtros opcionais combinados por AND, partial match case-insensitive
- **Motivo**: a restrição #9 pede busca por nome, documento e telefone, sem detalhar se os campos combinam entre si ou se é uma busca livre em um único campo de texto. Optou-se por reutilizar o endpoint de coleção (`GET /api/guests`) com três query params opcionais (`name`, `document`, `phone`); quando mais de um é informado, os critérios se combinam com AND (refinamento sucessivo, comportamento esperado de uma tela de busca avançada com múltiplos campos). Cada filtro é `LIKE` parcial e case-insensitive, via `Specification`/`JpaSpecificationExecutor` do Spring Data JPA. Sem nenhum filtro, retorna todos os hóspedes.
- **Alternativa descartada**: um único campo de busca livre (`query`) casando contra os três campos com OR — mais simples de implementar, mas menos preciso para o caso de uso de recepção (ex.: buscar por documento exato sem risco de bater com nome parecido).
- **Status**: suposição registrada; ajustável se o solicitante do desafio especificar comportamento diferente.

## [D-14] 2026-07-25 — Documentação de API (Swagger/OpenAPI) e base de conhecimento (Obsidian) como infraestrutura, fora do `feature_list.json`
- **Motivo**: solicitação explícita do usuário para adicionar documentação via Swagger/OpenAPI e uma base de conhecimento em Obsidian sobre os módulos. Como nenhuma funcionalidade de negócio foi implementada ainda, não há endpoints reais para documentar nem módulos de código para descrever em detalhe — por isso ambas foram tratadas como infraestrutura de apoio (não entram no fluxo de uma-funcionalidade-por-vez de `feature_list.json`) e devem se preencher/atualizar conforme as features F01+ forem implementadas.
- **Swagger/OpenAPI**: dependência `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3` (linha 3.x, compatível com Spring Boot 4 / Spring Framework 7 — a linha 2.x mais popular é para Spring Boot 3). Configuração mínima em `backend/src/main/java/com/projetosenior/gestaohospedes/config/OpenApiConfig.java` (apenas metadados de `Info`). UI em `/swagger-ui.html`, contrato bruto em `/v3/api-docs`, ambos habilitados por padrão. Validado com `./mvnw test` (build e contexto Spring sobem sem erro).
- **Obsidian**: vault em `docs/vault/` (fora de `backend/` e `frontend/`, para não ser build artifact de nenhum dos dois lados). Conteúdo inicial: visão geral do sistema, glossário de domínio, arquitetura/decisões-chave e mapa de funcionalidades, com uma nota por módulo de domínio futuro (Hóspede, Categoria de Quarto, Quarto, Reserva, Diária, Taxa de Estacionamento, Check-in/Check-out) ligadas por wikilinks. Não versiona pasta `.obsidian/` (configuração local do app, não conteúdo).
- **Status**: decisão confirmada pelo solicitante do desafio.
