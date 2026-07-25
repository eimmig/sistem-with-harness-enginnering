# Transferência de Sessão

Preencha isto ao final de cada sessão de trabalho. A próxima sessão deve conseguir retomar lendo só este arquivo + `progress.md` + `feature_list.json`.

## Bloqueios
- Nenhum. F13 implementado e passando. `feature_list.json` atualizado com evidência.
- F23 (repositório Git público) segue como a única pendência que não pode ser resolvida por código — fora do escopo de automação.

## Arquivos Tocados Nesta Sessão
- **Infraestrutura de frontend** (nova, reutilizável pelas próximas telas):
  - `frontend/proxy.conf.json` (novo): redireciona `/api` → `http://localhost:8080` em dev.
  - `frontend/angular.json`: `serve.options.proxyConfig` apontando para o proxy.
  - `frontend/src/app/app.config.ts`: `provideHttpClient()`, `provideAnimationsAsync()`.
  - `frontend/src/app/app.component.ts`/`.html`/`.scss`: virou um shell com `mat-toolbar` + navegação (`routerLink` para `/guests`, `/room-categories`, `/rooms`, `/reservations`, `/check-in`, `/check-out` — só `/guests` tem rota registrada ainda, as demais serão criadas junto de cada feature).
  - `frontend/src/app/app.component.spec.ts`: atualizado para o novo título/shell.
  - `frontend/package.json`: adicionado `@angular/animations` (necessário para `provideAnimationsAsync()`, não vinha no esqueleto).
  - `frontend/src/app/app.routes.ts`: rota `/guests` (lazy) + redirect da raiz.
- **F13**:
  - `frontend/src/app/features/guest/guest.model.ts` (novo): `Guest`, `GuestRequest`, `GuestSearchFilter`.
  - `frontend/src/app/features/guest/guest.service.ts` (novo): `create()`, `search()`.
  - `frontend/src/app/features/guest/guest-form/` (novo): `GuestFormComponent` + spec (4 testes).
  - `frontend/src/app/features/guest/guest-search/` (novo): `GuestSearchComponent` + spec (3 testes).
  - `frontend/src/app/features/guest/guests-page/` (novo): `GuestsPageComponent` (compõe form + search) + spec (2 testes).
- `DECISIONS.md`: D-23 registrada (infraestrutura de frontend).
- `feature_list.json`: F13 marcado `passing` com evidência (12/12 testes Karma + `ng build` sem erros + `./init.sh` completo + smoke test manual via `ng serve`/`curl`).
- `progress.md`, `docs/vault/Hóspede.md`, `docs/vault/Mapa de Funcionalidades.md`, `docs/vault/Arquitetura.md` (+ nova seção "Frontend"): atualizados.

## Nota técnica importante para próximas features
- `provideAnimationsAsync()` do Angular Material exige a dependência `@angular/animations` instalada explicitamente (`npm install @angular/animations@^19.2.0`) — o esqueleto gerado pelo `ng new` não a incluía; sem ela, `ng build` falha ao resolver `@angular/animations/browser` de forma lazy.
- Para testes de componentes que fazem HTTP, usar `provideHttpClient()` + `provideHttpClientTesting()` como `providers` no `TestBed.configureTestingModule` (API funcional moderna) em vez do `HttpClientTestingModule` (NgModule, deprecado).
- Verificação de UI neste ambiente não tem ferramenta de automação de navegador (Playwright/Puppeteer) — a verificação combina `ng build`, testes Karma em Chrome Headless real, e um smoke test manual via `ng serve` + `curl` no HTML servido. Documentado em D-23.
- Padrão de pastas estabelecido: `frontend/src/app/features/<domínio>/` com `*.service.ts`, `*.model.ts`, e um subdiretório por componente. Reaproveitar esse padrão para as próximas telas.

## Próxima Sessão
1. Ler `progress.md` (seção "Próximo Passo Recomendado") e `feature_list.json`.
2. Rodar `./init.sh` para confirmar que o ambiente ainda está saudável.
3. Próxima funcionalidade: **F14** (tela de configuração de preços por categoria/dia da semana) — depende de F03/F04, `passing`. Consumir `POST /api/room-categories` (cadastro) e `PUT /api/room-categories/{id}/prices` (configuração de preço — precisa dos 7 dias da semana preenchidos e positivos, ver D-16). Verificação: `room-category-price.component.spec.ts`.
4. Depois: F25 (gestão de quartos, depende de F24) → F15 (criação de reserva, depende de F05 **e** F25) → F16 (check-in) → F17 (check-out) → F18/F19 (listagens).
5. Atualizar `docs/vault/` antes do commit — parte obrigatória da Definição de Pronto (CLAUDE.md).
