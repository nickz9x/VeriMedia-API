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
- Fluxo de verificação: uma declaração pode ser aprovada, questionada ou rejeitada.
- Controle de acesso por papéis, garantindo que cada usuário execute somente as ações permitidas.
- Consulta pública de informações selecionadas, sem revelar dados sensíveis do criador.
- Documentação interativa da API via OpenAPI/Swagger.

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
4. Solicitar uma verificação.
5. Um usuário `VERIFIER` registra um parecer.
6. Opcionalmente, gerar um link público para consulta da procedência.

Rotas previstas:

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Cria um usuário. |
| `POST` | `/api/auth/login` | Autentica e retorna JWT. |
| `POST` | `/api/media` | Cadastra uma mídia. |
| `POST` | `/api/media/{id}/declaration` | Registra a declaração de origem. |
| `POST` | `/api/media/{id}/verification-request` | Solicita revisão. |
| `POST` | `/api/verification-requests/{id}/review` | Registra o parecer de um verificador. |
| `GET` | `/public/media/{token}` | Consulta pública de uma mídia compartilhada. |

## Documentação da API

Com a aplicação em execução, o Swagger deve estar disponível em:

```text
http://localhost:8080/swagger-ui/index.html
```

## Próximos passos

- Versionamento de mídias.
- Armazenamento de arquivos em S3 ou MinIO.
- Linha do tempo de auditoria.
- Links públicos com expiração e revogação.
- Testes de integração para autorização e verificação.

## Autor

Projeto de portfólio para praticar Java, Spring Data JPA, Spring Security, Bean Validation e OpenAPI em um domínio atual e orientado a regras de negócio.
