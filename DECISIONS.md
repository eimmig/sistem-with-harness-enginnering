# Decisões de Design

## [D-01] 2026-07-25 — Alerta de check-in antes das 14h é não bloqueante
- **Motivo**: o PDF diz "o sistema deverá emitir um alerta", não "impedir o check-in". Interpretação adotada: alerta de confirmação — o atendente vê o aviso e pode prosseguir mesmo assim.
- **Alternativa descartada**: bloquear completamente o check-in antes das 14h, sem opção de override.
- **Status**: suposição, a confirmar com o solicitante do desafio.

## [D-02] 2026-07-25 — Preço da diária de fim de semana usa o valor configurado para "Sábado"
- **Motivo**: a diária de fim de semana é um bloco único (sábado 14h → segunda 12h), mas a tela de configuração pedida permite preço por dia individual (segunda a domingo). Como domingo nunca inicia uma diária própria dentro dessa regra, adotamos o preço configurado para sábado como o preço da diária combinada.
- **Alternativa descartada**: criar um campo de preço "fim de semana" separado dos 7 dias da semana — mais simples de implementar, porém contraria o pedido explícito de "configurar o valor de cada dia da semana".
- **Status**: suposição, a confirmar.

## [D-03] 2026-07-25 — Taxa de estacionamento no fim de semana é cobrada uma única vez
- **Motivo**: decorre diretamente de D-02 — se sábado+domingo formam uma única diária, a taxa de estacionamento correspondente (R$ 20,00) também é cobrada uma única vez para o bloco inteiro, não por noite.
- **Status**: suposição, a confirmar.

## [D-04] 2026-07-25 — Java 17 como versão alvo (compatível com "Java 11 ou superior")
- **Motivo**: Spring Boot 3.x (linha atual estável) exige Java 17+. A JDK 25 está instalada localmente; o `pom.xml` fixa `<java.version>17</java.version>` para manter compatibilidade ampla com ambientes de avaliação que talvez não tenham a JDK mais recente, mesmo compilando localmente com uma JDK mais nova.

## [D-05] 2026-07-25 — Reserva referencia uma CategoriaQuarto, não um quarto físico individual
- **Motivo**: a especificação não pede controle de disponibilidade de quartos individuais, numeração de quartos nem inventário — apenas preço configurável por categoria e por dia da semana. Modelar quartos físicos individuais seria escopo não solicitado.
- **Status**: decisão de escopo (não é uma suposição sobre requisito ambíguo).

## [D-06] 2026-07-25 — Testes de frontend com Karma + Jasmine (padrão do Angular CLI)
- **Motivo**: zero configuração adicional — já vem pronto com `ng new`, e o Chrome está instalado localmente para rodar os testes em modo headless. A alternativa (Jest) reduziria a dependência de navegador, mas exigiria configuração extra não pedida pelo desafio.

## [D-07] 2026-07-25 — Monorepo único (backend/ + frontend/) em vez de dois repositórios
- **Motivo**: a especificação pede "criar repositório em um repositório GIT" (singular) e um único link para avaliação. Um monorepo simplifica isso sem ferir nenhum requisito técnico.

## [D-11] 2026-07-25 — Harness reconciliado com `skills/harness-creator` do repositório do curso
- **Motivo**: o curso que motivou este projeto (`learn-harness-engineering`) inclui uma skill própria com um script de validação (`validate-harness.mjs`) que pontua o harness em 5 subsistemas. A primeira versão do harness deste projeto (`AGENTS.md` + `FEATURES.md` + `PROGRESS.md` + `DECISIONS.md`, sem `init.sh` nem `session-handoff.md`) pontuou 28/100 — não porque a substância estivesse errada, mas porque faltavam artefatos reais que o validador (corretamente) exige: um entrypoint de verificação executável e um arquivo de handoff dedicado.
- **Mudanças**: `FEATURES.md` (tabela Markdown) foi substituído por `feature_list.json` (schema com `id`/`name`/`description`/`status`/`verification`/`evidence`/`dependencies`) para evitar ter duas fontes da mesma informação; `PROGRESS.md` virou `progress.md` (nome exato esperado pela ferramenta, e portável entre sistemas de arquivos case-sensitive); `session-handoff.md` e `init.sh` foram criados do zero.
- **Alternativa descartada**: manter os dois formatos (Markdown para humanos + JSON para a ferramenta) em paralelo — rejeitada por violar o princípio de fonte única da verdade (uma tabela e um JSON descrevendo o mesmo backlog inevitavelmente divergem com o tempo).
- **Atualização**: os títulos bilíngues que `AGENTS.md`, `progress.md` e `session-handoff.md` ganharam nessa reconciliação (ex.: "Startup Workflow", "Definition of Done") existiam só para o `validate-harness.mjs` reconhecer as seções por substring em inglês — o validador foi usado como teste pontual, não como requisito do desafio. Foram revertidos para português puro logo em seguida; os artefatos em si (`init.sh`, `feature_list.json`, `session-handoff.md`) permanecem, só o texto voltou a ser 100% português. Rodar `validate-harness.mjs` de novo após essa reversão volta a mostrar uma pontuação mais baixa nesses subsistemas — esperado e aceito.

## [D-09] 2026-07-25 — PostgreSQL do Docker exposto na porta 5433, não 5432
- **Motivo**: durante a validação da inicialização, descobrimos um PostgreSQL nativo já instalado e escutando na porta 5432 desta máquina, disputando a porta com o container Docker. O backend acabava conectando no Postgres nativo (sem o banco `gestao_hospedes`) em vez do container, causando `FATAL: banco de dados "gestao_hospedes" não existe`. Ambientes de desenvolvedores/avaliadores frequentemente têm um Postgres local instalado, então evitar a porta padrão é mais robusto. `docker-compose.yml` mapeia `5433:5432`; `application.properties` aponta para `localhost:5433`.
- **Como foi descoberto**: `Get-NetTCPConnection -LocalPort 5432` mostrou dois processos escutando na mesma porta — o proxy do Docker Desktop e um `postgres.exe` nativo.

## [D-10] 2026-07-25 — Credenciais do banco parametrizadas via variável de ambiente
- **Motivo**: o linter do editor sinalizou a senha fixa `postgres` em `application.properties` como segredo hardcoded. Trocado para `${DB_USER:postgres}` / `${DB_PASSWORD:postgres}` (Spring) e `${DB_USER:-postgres}` / `${DB_PASSWORD:-postgres}` (docker-compose) — mantém o valor padrão funcionando sem configuração extra localmente, mas permite sobrescrever via variável de ambiente em outros ambientes, sem precisar versionar nenhuma credencial real.

## [D-08] 2026-07-25 — Spring Boot 4.1.0 (versão atual do Spring Initializr) e H2 para testes
- **Motivo**: o Spring Initializr gerou o projeto na versão estável atual, Spring Boot 4.1.0 (compatível com "Java 11 ou superior" e mais recente que a linha 3.x mencionada inicialmente no `AGENTS.md` — corrigido lá).
- **Testes**: `src/main/resources/application.properties` aponta para o PostgreSQL do `docker-compose.yml`; `src/test/resources/application.properties` aponta para H2 em memória. Isso permite que `./mvnw test` funcione sem depender do Docker estar rodando — o Postgres real só é necessário para `spring-boot:run`. Testes de integração mais realistas com Postgres via Testcontainers ficam como evolução futura, não implementada nesta fase de inicialização (evitar overengineering do esqueleto).
