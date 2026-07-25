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
  | Status do quarto: Disponível / Sujo / Ocupado | `RoomStatus`: `AVAILABLE` / `DIRTY` / `OCCUPIED` |
- **Status**: decisão confirmada pelo solicitante do desafio.

## [D-28] 2026-07-25 — F17: `GET /api/reservations/pending-check-out` (mesmo padrão de D-27); detalhamento (regra #8) exibido como resultado pós-ação, não prévia separada
- **Motivo (endpoint)**: mesmo raciocínio de D-27 — expõe `findByActualCheckInIsNotNullAndActualCheckOutIsNull()`, já existente no `ReservationRepository` desde F10/F11, agora também via `ReservationController`.
- **Motivo (detalhamento)**: como o backend já decidiu em D-22 que o check-out é uma chamada única que calcula e persiste ao mesmo tempo (sem prévia), a tela reflete essa mesma decisão: ao clicar "Fazer check-out", a reserva sai da lista de pendentes e o detalhamento completo (diárias + estacionamento + taxa de atraso + total, `CheckOutResponse`) aparece numa seção "Detalhamento dos check-outs realizados" — cumprindo a regra #8 (exibir o detalhamento) como confirmação visual do que foi cobrado, não como um passo interativo antes de uma ação separada de confirmar.
- **Status**: suposição registrada; ajustável se o solicitante do desafio quiser um passo de prévia explícito antes de confirmar o check-out.

## [D-27] 2026-07-25 — F16: tela de check-in lista reservas pendentes via `GET /api/reservations/pending-check-in` (novo); aviso das 14h é um estado inline na própria linha, não um modal
- **Motivo (endpoint)**: F15 só criava reservas; nenhuma tela ainda listava reservas existentes. F16 precisa mostrar ao atendente quais reservas estão aguardando check-in. Em vez de um endpoint novo com lógica nova, o `GET /api/reservations/pending-check-in` só expõe `ReservationRepository.findByActualCheckInIsNull()` — método que já existia desde F10/F11 (usado internamente por `GuestController`, nunca exposto via `ReservationController`). Mesmo padrão de D-24/D-25: endpoint adicionado dentro do escopo da tela que precisa dele.
- **Motivo (aviso inline, não modal)**: a regra #6 exige aviso + confirmação explícita antes das 14h (D-01). Em vez de abrir um `MatDialog`, o fluxo é: o atendente clica "Fazer check-in" → `POST .../check-in` com `confirmedByAttendant=false` → se o backend responder `400` (antes das 14h), a linha da tabela troca o botão por um aviso + botões "Confirmar"/"Cancelar" → "Confirmar" reenvia com `confirmedByAttendant=true`. Mais simples que um modal, sem dependência extra, e o aviso fica visualmente ligado à reserva específica (relevante se o atendente estiver processando várias linhas).
- **Diferenciação de erros HTTP**: `400` → mostra o aviso de confirmação (não é bem um "erro", é um passo do fluxo); `409` → mensagem de erro (quarto indisponível ou já com check-in); outros → mensagem genérica.
- **Status**: suposições registradas; ajustáveis se o solicitante do desafio quiser um modal de confirmação em vez do fluxo inline.

## [D-26] 2026-07-25 — F15: busca de hóspede por nome (sem autocomplete), datas via `<input type="datetime-local">`, todos os quartos no seletor (não só `AVAILABLE`)
- **Motivo (busca de hóspede)**: não há um "seletor de todos os hóspedes" viável (lista pode crescer sem limite); reaproveita `GuestService.search()` (F02) com um campo de busca por nome + botão, mostrando resultados clicáveis para selecionar — mais simples que `mat-autocomplete` e reaproveita 100% do endpoint já existente, sem exigir mudança no backend.
- **Motivo (datas)**: Angular Material não tem um seletor de data+hora combinado pronto; em vez de integrar uma biblioteca extra ou compor date-picker + time-picker manualmente, usa-se o input nativo HTML5 `type="datetime-local"`, cujo formato de string (`YYYY-MM-DDTHH:mm`) é aceito diretamente pelo binding Jackson de `LocalDateTime` no backend sem conversão adicional no frontend.
- **Motivo (todos os quartos no seletor)**: consistente com D-18 — a criação de reserva não valida status do quarto (só o check-in valida `AVAILABLE`), então a tela não filtra por status; mostrar só quartos `AVAILABLE` esconderia quartos que estão `OCCUPIED`/`DIRTY` hoje mas ficarão livres na data futura da reserva.
- **Status**: suposições registradas; ajustáveis se o solicitante do desafio quiser autocomplete de hóspede ou um date-picker dedicado.

## [D-25] 2026-07-25 — F25 adicionou `GET /api/rooms` ao backend (mesmo padrão de D-24)
- **Motivo**: F24 só implementou `POST /api/rooms` (criar, nasce `AVAILABLE`) e `PATCH /api/rooms/{id}/status` (alterar status) — nenhuma delas listava quartos existentes. A tela de gestão de quartos (F25) precisa exibir a lista de quartos cadastrados, com um seletor de status por linha para alteração inline. Endpoint adicionado dentro do escopo de F25, mesmo raciocínio de D-24.
- **UI**: `RoomListComponent` mostra uma tabela com número, categoria e um `mat-select` de status por linha; ao trocar, chama `PATCH /api/rooms/{id}/status` imediatamente (sem botão de confirmar separado — mudança de status é uma ação simples e reversível, diferente de check-in/check-out que têm regras de negócio mais pesadas).
- **Status**: decisão técnica, não sujeita a confirmação do solicitante do desafio.

## [D-24] 2026-07-25 — F14 adicionou `GET /api/room-categories` ao backend (não estava em nenhuma feature anterior)
- **Motivo**: F03/F04 implementaram só `POST /api/room-categories` (criar) e `PUT /api/room-categories/{id}/prices` (configurar preço) — nenhuma delas precisava listar categorias existentes. A tela de configuração de preços (F14) precisa que o atendente escolha, entre as categorias já cadastradas, qual delas vai ter o preço configurado — o que exige uma forma de listá-las. Como esse endpoint é estritamente necessário para a própria F14 (não é "já que estou aqui, vou mexer em outra coisa"), foi adicionado dentro do escopo desta feature, em vez de virar uma nova entrada no backlog.
- **Endpoint**: `GET /api/room-categories`, sem filtros, retorna todas as categorias (mesmo padrão de `RoomCategoryResponse` usado por `POST`/`PUT`, incluindo os preços já configurados — permite a tela pré-popular os campos ao selecionar uma categoria).
- **Status**: decisão técnica, não sujeita a confirmação do solicitante do desafio.

## [D-23] 2026-07-25 — Infraestrutura do frontend: proxy para o backend, componentes standalone lazy-loaded, estrutura por feature
- **Motivo**: o esqueleto Angular criado na inicialização do projeto era só o placeholder padrão do `ng new` (sem `HttpClient`, sem tema aplicado além do CSS prebuilt, sem rotas) — precisou de infraestrutura mínima antes da primeira tela de negócio (F13).
- **Chamadas à API via caminho relativo `/api/...`** (não URL absoluta `http://localhost:8080/...`): evita configurar CORS no backend. Em desenvolvimento, `frontend/proxy.conf.json` redireciona `/api` para `http://localhost:8080` (configurado em `angular.json` → `serve.options.proxyConfig`); só funciona com `ng serve`, não com o build de produção servido estaticamente — aceitável porque a avaliação do desafio roda localmente via `ng serve`/`README.md`.
- **`provideHttpClient()` + `provideAnimationsAsync()`** adicionados em `app.config.ts`; `@angular/animations` precisou ser adicionado como dependência explícita (`package.json`) — não vinha instalado no esqueleto e o build falhava ao resolver `@angular/animations/browser` de forma lazy.
- **Componentes standalone com rotas lazy (`loadComponent`)**, sem NgModules — padrão do Angular 19 usado desde a inicialização do projeto (backend já segue o equivalente em português/inglês via D-13).
- **Estrutura por feature**: `frontend/src/app/features/<domínio>/` (ex.: `guest/`), com um `*.service.ts` (chamadas HTTP), um `*.model.ts` (interfaces TS espelhando os DTOs do backend) e um componente por responsabilidade dentro de subpastas (`guest-form/`, `guest-search/`), compostos por um componente de página (`guests-page/`) que registra a rota. Nomes de arquivo/classe de componente batem com os exigidos no campo `verification` de `feature_list.json` (ex.: `guest-form.component.spec.ts`).
- **`data-testid` nos elementos interativos** (inputs, botões, tabelas) — facilita seletores estáveis em testes, independentes de texto visível (que é em português) ou classes de estilo.
- **Texto de UI em português** (labels, mensagens de erro/vazio), identificadores de código em inglês — mesma convenção do backend (D-13), estendida ao frontend.
- **Verificação de UI**: sem ferramenta de automação de navegador neste ambiente; a verificação combina `ng build` (compila e resolve todos os imports), testes Karma/Jasmine rodando em Chrome Headless real (renderiza os componentes de fato, não simulado) e um smoke test manual via `ng serve` + `curl` no `index.html`. Não substitui clique-a-clique num navegador real, mas é a melhor cobertura disponível nas ferramentas deste ambiente.
- **Status**: decisões de infraestrutura, não sujeitas a confirmação do solicitante do desafio (technical, não de negócio).

## [D-22] 2026-07-25 — Check-out: chamada única (sem endpoint de prévia); "diária vigente" da taxa de atraso é a última noite hospedada; quarto vai para `DIRTY` (não `AVAILABLE`)
- **Motivo (chamada única)**: a regra #8 pede exibir o detalhamento completo antes de confirmar. Em vez de um par prévia (`GET`, sem persistir) + confirmação (`POST`, persiste), optou-se por uma única chamada (`POST /api/reservations/{id}/check-out`) que já calcula e devolve o detalhamento completo (diárias + estacionamento + taxa de atraso + total) no mesmo response em que persiste o check-out — mesmo padrão de F08 (uma chamada, sem prévia separada). "Exibir antes de confirmar" fica a cargo do frontend (F17): o atendente só aciona esse endpoint depois de revisar os valores na tela, mas o backend não modela um estado intermediário "prévia calculada, ainda não confirmada".
- **Motivo ("diária vigente")**: a regra #7 cobra "50% do valor da diária vigente" no check-out após 12h, mas não define qual diária é essa. Seguindo a convenção de D-19 (cada diária pertence ao dia em que **começa**), a diária vigente no momento do check-out é a última noite hospedada — ou seja, o dia da semana de `actualCheckOut.toLocalDate().minusDays(1)`. Isso mantém consistência: é a diária cujo limite (12h do dia seguinte) está sendo ultrapassado.
- **Motivo (status do quarto)**: após o check-out, o quarto não pode ir direto para `AVAILABLE` — precisa de limpeza antes do próximo hóspede (D-12: `SUJO` é um status válido e distinto de `DISPONIVEL`). Por isso o check-out muda o quarto para `RoomStatus.DIRTY`, não `AVAILABLE`; a transição `DIRTY → AVAILABLE` fica para uma ação de limpeza futura (fora do escopo de F09, não pedida pela especificação).
- **Cálculo**: `dailyRateTotal` e `parkingFeeTotal` usam `Reservation.actualCheckIn` até "agora" (não `expectedCheckOut`), reaproveitando `DailyRateService`/`ParkingFeeService` de F06/F07 sem modificação. Guardas: 409 se a reserva ainda não teve check-in (`actualCheckIn == null`); 409 se já teve check-out (`actualCheckOut != null`); 404 se a reserva não existir.
- **Status**: suposições registradas; ajustáveis se o solicitante do desafio quiser um fluxo de prévia separado, outra diária de referência para a taxa de atraso, ou outro status final do quarto.

## [D-21] 2026-07-25 — Check-in usa `Clock` injetável para "agora"; `actualCheckIn` novo em `Reservation`; quarto indisponível é 409, confirmação ausente é 400
- **Motivo**: a regra #6 compara o horário **do momento do check-in** (não o horário previsto da reserva) com 14h00 — por isso `ReservationController` passou a depender de um `java.time.Clock` (bean `Clock.systemDefaultZone()` em produção) em vez de `LocalDateTime.now()` direto, para que os testes possam controlar "agora" de forma determinística (mock do `Clock`, sem `Thread.sleep` nem testes frágeis por horário do relógio da máquina).
- **`Reservation.actualCheckIn`**: novo campo (`LocalDateTime`, nullable) que registra quando o check-in de fato ocorreu — necessário porque `expectedCheckIn`/`expectedCheckOut` são só a previsão da reserva (F05). Será reaproveitado por F09 (check-out) e F10/F11 (listagens: hóspede "no hotel" = `actualCheckIn` preenchido e sem check-out; "sem check-in" = `actualCheckIn` nulo).
- **Códigos HTTP**: quarto com status diferente de `AVAILABLE` → `409 CONFLICT` (conflito com o estado atual do recurso); check-in antes das 14h sem `confirmedByAttendant=true` no corpo da requisição → `400 BAD_REQUEST` (a UI deve exibir o aviso e reenviar com confirmação — ver F16); reserva já com check-in feito → `409 CONFLICT` (guarda contra check-in duplicado, não pedido explicitamente mas decorre diretamente da regra #6 combinada com o significado de "quarto ocupado").
- **Endpoint**: `POST /api/reservations/{id}/check-in`, corpo opcional `{"confirmedByAttendant": boolean}` (ausência de corpo = `false`). Ao confirmar, o quarto muda para `OCCUPIED`.
- **Status**: suposições registradas; ajustáveis se o solicitante do desafio quiser outro código HTTP ou outro formato de confirmação.

## [D-20] 2026-07-25 — Campo "hóspede tem carro e usa vaga" mora em `Reservation`; classificação dia útil/fim de semana da taxa de estacionamento é fixa (segunda-sexta vs sábado-domingo), independente do preço por dia configurado em F04
- **Motivo (onde mora o campo)**: pendência deixada por D-18. `Reservation.parkingRequested` (boolean, default `false`) é o lugar mais natural — é uma decisão por estadia, capturada no mesmo momento em que se sabe quarto/datas (criação da reserva), e reaproveita as mesmas `expectedCheckIn`/`expectedCheckOut` que `DailyRateService` já usa para contar noites. `ReservationRequest` ganhou o campo `parkingRequested` (opcional, default `false` quando ausente no JSON); `ReservationResponse` passou a expor o valor.
- **Motivo (classificação dia útil/fim de semana)**: diferente do preço da diária (F04, configurável dia a dia), a regra #5 dá valores fixos: R$ 15 "em diária de dia útil", R$ 20 "em diária de fim de semana". A regra #2 já define explicitamente "dia útil (segunda a sexta)" — por isso `ParkingFeeService` classifica cada noite como dia útil (segunda-sexta → R$ 15) ou fim de semana (sábado-domingo → R$ 20), usando `EnumSet.of(SATURDAY, SUNDAY)` para o fim de semana. Isso é independente da regra #3 (que descreve como a diária de fim de semana é *decomposta* em 3 diárias individuais para fins de F06, não uma reclassificação de qual dia é "fim de semana" para taxas fixas).
- **Alternativa descartada**: tratar a diária que começa sexta-feira como "fim de semana" para a taxa de estacionamento (por causa da linguagem da regra #3) — rejeitada porque a regra #2 já classifica sexta como dia útil sem ambiguidade, e a regra #3 fala apenas da composição da diária, não de taxas fixas.
- **Status**: suposições registradas; ajustáveis se o solicitante do desafio classificar sexta-feira como fim de semana para a taxa de estacionamento.

## [D-19] 2026-07-25 — `DailyRateService` é um serviço puro, sem endpoint próprio; noite é atribuída ao dia-da-semana do seu check-in
- **Motivo**: a verificação de F06 pede só `DailyRateServiceTest`, sem controller — o cálculo de diária é um bloco de lógica de negócio reutilizável, que será consumido por check-in/check-out (F08/F09) e não precisa de endpoint HTTP próprio nesta etapa. Algoritmo: número de noites = diferença em dias de calendário entre a data do check-in e a data do check-out (`ChronoUnit.DAYS.between`); cada noite é atribuída ao dia da semana em que ela **começa** (ex.: a diária que vai de sexta 14h a sábado 12h é "sexta"), e seu preço vem de `RoomCategory.prices` (regra #1 e #3 — bate exatamente com o exemplo da regra #3: sex→sáb→dom→seg 12h = 3 diárias, sexta+sábado+domingo). Isso implementa D-02 diretamente: fim de semana não é tratado como bloco, é a soma de diárias individuais com preços diferentes.
- **Assinatura**: `calculate(RoomCategory, LocalDateTime checkIn, LocalDateTime checkOut)` — recebe `RoomCategory` e datas diretamente, não um `Reservation`, para poder ser reutilizado tanto com as datas *previstas* de uma reserva (F06) quanto com as datas *reais* de check-in/check-out quando F08/F09 as adicionarem.
- **Erros**: `IllegalArgumentException` se `checkOut` não é pelo menos um dia de calendário após `checkIn`; `IllegalStateException` se a categoria não tem preço configurado para algum dos dias envolvidos (nunca deveria acontecer se F04 for respeitada, mas evita `NullPointerException` silencioso).
- **Status**: suposição registrada; ajustável se o solicitante do desafio quiser o cálculo exposto via endpoint HTTP já nesta etapa.

## [D-18] 2026-07-25 — Criação de reserva não valida status do quarto; disponibilidade só é checada no check-in
- **Motivo**: a regra #6 (quarto precisa estar `DISPONIVEL`) é explicitamente sobre check-in, não sobre criação de reserva. Como o sistema não modela um calendário de disponibilidade por data (o status do quarto é um único campo mutável, não uma agenda), exigir `DISPONIVEL` no momento da reserva impediria reservar um quarto hoje para uma data futura enquanto ele está ocupado por outro hóspede agora — comportamento normal de hotel. Por isso `POST /api/reservations` só valida que hóspede e quarto existem (404 se não) e que `expectedCheckOut` é depois de `expectedCheckIn` (400 se não); o gate de `DISPONIVEL` fica exclusivamente no check-in (F08).
- **Alternativa descartada**: bloquear reserva se `room.status != DISPONIVEL` — rejeitada por não corresponder ao uso real (reservas são feitas com antecedência).
- **Escopo não incluído**: taxa de estacionamento (regra #5) depende de "hóspede tiver carro e usar vaga" — não há campo para isso em `Reservation` ainda; decisão adiada para quando F07 (cálculo de taxa de estacionamento) for implementada, já que não há consenso ainda sobre se essa informação é capturada na reserva ou no check-in.
- **Status**: suposição registrada; ajustável se o solicitante do desafio pedir checagem de disponibilidade na criação da reserva.

## [D-17] 2026-07-25 — Número do quarto é `String`; status muda via `PATCH /api/rooms/{id}/status` já em F24
- **Motivo**: número de quarto é tratado como rótulo, não quantidade (ex.: "101", "204B"), então `Room.number` é `String`, sem validação de formato específica além de não-vazio. Quanto ao status: a descrição de F24 em `feature_list.json` já inclui "permitir alterar o status" no escopo do backend (diferente de F03/F04, que dividiram cadastro e configuração de preço em duas features separadas) — por isso o endpoint `PATCH /api/rooms/{id}/status` foi implementado dentro de F24, e não adiado para F25 (que é só a tela). Todo quarto criado nasce com status `AVAILABLE` (regra implícita: quarto recém-cadastrado está pronto para uso).
- **Status**: suposição registrada; ajustável se o solicitante do desafio especificar formato de número diferente (ex.: inteiro) ou status inicial diferente.

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
