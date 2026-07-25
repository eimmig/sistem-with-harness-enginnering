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

## Documentação da API
Adicionado `springdoc-openapi-starter-webmvc-ui` (versão 3.0.3, compatível com Spring Boot 4 / Spring Framework 7) como infraestrutura — não é uma funcionalidade rastreada em `feature_list.json`. Configuração em `backend/src/main/java/com/projetosenior/gestaohospedes/config/OpenApiConfig.java`. A documentação se preenche automaticamente conforme os controllers de cada feature forem implementados; atualmente cobre `POST`/`GET /api/guests` (F01/F02) e `POST /api/room-categories` (F03).

Ver também: [[Visão Geral do Sistema]], [[Mapa de Funcionalidades]].
