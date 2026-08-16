# VeriMedia API

API REST para registrar a procedência de conteúdos digitais e tornar transparente o uso de inteligência artificial em imagens, vídeos, áudios ou documentos.

O sistema permite que um criador cadastre uma mídia, declare como ela foi produzida e solicite uma verificação. Usuários autorizados podem revisar essa declaração, enquanto uma consulta pública pode mostrar informações selecionadas sobre o conteúdo sem expor dados privados.

## Qual problema o projeto resolve?

Com a popularização de ferramentas de IA generativa, tornou-se mais difícil entender a origem de uma mídia: ela foi criada por uma pessoa, gerada por IA ou sofreu alguma modificação artificial?

O VeriMedia não promete identificar deepfakes automaticamente. Ele cria uma trilha de confiança: registra a declaração do criador, guarda informações de integridade do arquivo e permite um processo de revisão por verificadores.

## O que o software tem de especial?

Este projeto vai além de um CRUD tradicional. Seus diferenciais são:

- Registro da origem da mídia: humana, assistida por IA, gerada por IA ou manipulada por IA.
- Cálculo de hash do arquivo para verificar se ele foi alterado após o cadastro.
- Versionamento append-only: atualizar uma mídia cria uma nova versão imutável; cada versão mantém seu `publicToken` para sempre.
- Fluxo de verificação: uma declaração pode ser aprovada, questionada ou rejeitada.
- Controle de acesso por papéis, garantindo que cada usuário execute somente as ações permitidas.
- Consulta pública de informações selecionadas, sem revelar dados sensíveis do criador.
- Documentação interativa da API via OpenAPI/Swagger (planejado).

## Decisões de domínio — integridade e versionamento

O design de integridade segue o princípio **append-only** ("estilo git"): nada é editado, tudo é acrescentado.

| # | Decisão | Justificativa |
| --- | --- | --- |
| D1 | Mídia imutável após o registro | Hash só prova integridade se a referência for estável |
| D2 | Modificação = nova versão (`version` + `parentMedia`) | Nunca editar, sempre acrescentar |
| D3 | Cada versão tem seu `publicToken` estável e eterno | Citações publicadas continuam verificáveis para sempre |
| D4 | A última versão é resolvida por endpoint autenticado; token nunca é reapontado | Separa endereço permanente de consulta atual |
| D5 | Arquivo no disco: uma pasta por mídia, nome gerado pelo servidor (UUID) | Evita path traversal, colisões e caracteres inválidos |
| D6 | SHA-256 para integridade; comparação com `MessageDigest.isEqual` | Hash determinístico e comparação em tempo constante |
| D7 | Verificação via endpoint público por `publicToken` | Consulta pública sem expor dados do criador |
| D8 | Diretório de storage configurável no `application.yaml`; volume no docker-compose; `storage/` no `.gitignore` | Arquivos de mídia fora do git |

## Perfis de usuário

| Perfil | Responsabilidades |
| --- | --- |
| `CREATOR` | Cadastra mídias, cria declarações e solicita verificações. |
| `VERIFIER` | Analisa e registra pareceres sobre declarações. |
| `ADMIN` | Gerencia a plataforma e consulta registros administrativos. |
| `PUBLIC` | Consulta conteúdos compartilhados publicamente. |

## Tecnologias

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Bean Validation
- OpenAPI / Swagger
- Docker Compose

## Como executar

### Pré-requisitos

- Java 21
- Maven ou Maven Wrapper
- Docker Desktop em execução

### 1. Suba o PostgreSQL

Na raiz do projeto:

```bash
docker compose up -d
```

### 2. Confira a configuração

Em `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/veriMediaDb
    username: user
    password: postgres

  jpa:
    hibernate:
      ddl-auto: update
```

### 3. Execute a aplicação

No Windows:

```powershell
.\mvnw spring-boot:run
```

Ou com Maven instalado:

```bash
mvn spring-boot:run
```

A API inicia, por padrão, em `http://localhost:8080`.

## Como utilizar

Fluxo principal:

1. Criar uma conta ou fazer login e receber um token JWT.
2. Enviar uma mídia.
3. Declarar sua origem e informar se houve uso de IA.
4. Para atualizar a mídia, criar uma nova versão (`POST /api/media/{id}/version`) — a anterior permanece intacta e verificável.
5. Solicitar uma verificação.
6. Um usuário `VERIFIER` registra um parecer.
7. Opcionalmente, gerar um link público para consulta da procedência.

Rotas da API:

| Método | Rota | Descrição | Acesso |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Cria um usuário. | Público |
| `POST` | `/api/auth/login` | Autentica e retorna JWT. | Público |
| `POST` | `/api/media/register` | Cadastra uma mídia (multipart: arquivo + dados da declaração). | `CREATOR` |
| `POST` | `/api/media/{id}/version` | Cria uma nova versão da mídia; a anterior permanece intacta. | `CREATOR` (dono) |
| `GET` | `/api/media` | Lista todas as mídias. | `ADMIN`, `VERIFIER` |
| `GET` | `/api/media/search/pending` | Lista mídias pendentes de revisão. | `ADMIN`, `VERIFIER` |
| `GET` | `/api/media/search/rejected` | Lista mídias rejeitadas. | `ADMIN`, `VERIFIER` |
| `GET` | `/api/media/search/verified` | Lista mídias verificadas. | `ADMIN`, `VERIFIER` |
| `POST` | `/api/media/review` | Registra o parecer de um verificador. | `ADMIN`, `VERIFIER` |
| `POST` | `/api/media/request-review/{publicToken}` | Solicita revisão de uma mídia. | `CREATOR`, `VERIFIER` |
| `GET` | `/api/media/request-review` | Lista solicitações de revisão. | `ADMIN`, `CREATOR` |
| `GET` | `/api/media/public/search/{publicToken}` | Consulta pública de uma mídia compartilhada. | Público |

## Documentação da API

Documentação interativa via springdoc-openapi/Swagger está planejada (Fase 4 do `TODO.md`). Por enquanto, este README é a referência de rotas.

## Próximos passos

- Hash SHA-256 real + gravação dos arquivos em disco (Fase 1, Etapa 2 do `TODO.md`).
- Endpoint público de verificação de integridade: `POST /api/media/public/verify/{publicToken}`.
- Testes automatizados do fluxo de registro, versão e verificação.
- Armazenamento de arquivos em S3 ou MinIO.
- Linha do tempo de auditoria.
- Links públicos com expiração e revogação.

## Autor

Projeto de portfólio para praticar Java, Spring Data JPA, Spring Security, Bean Validation e OpenAPI em um domínio atual e orientado a regras de negócio.
