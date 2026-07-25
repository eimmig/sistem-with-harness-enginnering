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
