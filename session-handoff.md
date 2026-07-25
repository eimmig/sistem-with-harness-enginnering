# Transferência de Sessão

Preencha isto ao final de cada sessão de trabalho. A próxima sessão deve conseguir retomar lendo só este arquivo + `progress.md` + `feature_list.json`.

## Bloqueios
- Nenhum. Todas as decisões de negócio pendentes (D-01, D-02, D-03, D-05) foram confirmadas pelo solicitante do desafio nesta sessão. `feature_list.json` já foi revisado para refletir as mudanças. Pronto para começar `F01`.

## Arquivos Tocados Nesta Sessão
- `backend/pom.xml`: dependência `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3` adicionada (linha 3.x, compatível com Spring Boot 4 / Spring Framework 7).
- `backend/src/main/java/.../config/OpenApiConfig.java`: criado (metadados básicos de `Info` para o Swagger UI).
- `docs/vault/`: base de conhecimento Obsidian criada do zero — `Início.md`, `Visão Geral do Sistema.md`, `Glossário de Domínio.md`, `Arquitetura.md`, `Mapa de Funcionalidades.md` e uma nota por módulo futuro (`Hóspede`, `Categoria de Quarto`, `Quarto`, `Reserva`, `Diária`, `Taxa de Estacionamento`, `Check-in e Check-out.md`), ligadas por wikilinks.
- `DECISIONS.md`: D-14 adicionada (justifica tratar Swagger/OpenAPI e Obsidian como infraestrutura, fora do fluxo de `feature_list.json`).
- `README.md`: seções "Documentação da API (Swagger / OpenAPI)" e "Base de Conhecimento (Obsidian)" adicionadas; stack e árvore de diretórios atualizadas.
- `progress.md`: itens concluídos desta sessão registrados.
- `.gitignore`: `docs/vault/.obsidian/` ignorado (config local do app, não conteúdo).
- Nenhuma mudança em `feature_list.json` — Swagger e Obsidian não viraram itens rastreados (decisão do usuário, ver D-14). `activeFeature` continua `null`, nenhuma funcionalidade de negócio implementada.

## Próxima Sessão
1. Ler `progress.md` (seção "Próximo Passo Recomendado") e `feature_list.json`.
2. Rodar `./init.sh` para confirmar que o ambiente ainda está saudável (passou nesta sessão: backend compila/testa com Swagger incluso, frontend builda e os 3 testes passam).
3. Marcar `F01` como `"status": "active"` em `feature_list.json` e começar a implementação — uma funcionalidade por vez, sem avançar para a próxima antes de `F01` estar `passing` com evidência registrada. Lembrar: código em inglês (D-13), domínio documentado em português. Ao criar o `GuestController`, ele já vai aparecer automaticamente no Swagger UI (`/swagger-ui.html`) sem configuração extra.
