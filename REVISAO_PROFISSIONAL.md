# Revisão Profissional — VeriMedia API

> Data: 2026-08-15 (re-revisão completa sobre o código atual)
> Escopo: repositório externo `vca`, commit `287b868` (em sync com o GitHub)
> Este arquivo substitui a versão antiga que estava em `VeriMedia API/REVISAO_PROFISSIONAL.md`.

---

## ✅ Itens já resolvidos (desde a primeira revisão)

| Item | Status |
|---|---|
| 1. Header do token | ✅ `HttpHeaders.AUTHORIZATION` — **mas o fix ainda não foi commitado!** |
| 2. Token sem subject | ✅ `.withSubject(user.getLogin())` |
| 3. `permitAll()` geral | ✅ `requestMatchers` públicos + `.anyRequest().authenticated()` + `@PreAuthorize` nos services |
| 10. Bean Validation | ✅ dependência + `@Valid` + anotações nos DTOs + handler |
| 11. Tratamento global de erros | ✅ `GlobalExceptionHandler` criado (com ressalva — ver seção 🔴) |
| 14. Enum `Role` | ✅ `CREATOR`, `VERIFIER`, `ADMIN` — alinhado ao domínio |
| 16. `UserLoginDto(String Login)` | ✅ virou `login` |
| 4. Escalada de privilégio | ✅ sem `role` no DTO; service força `CREATOR` (2026-08-15) |
| 5. Senha no response | ✅ `UserResponseDto` + `UserMapper` (falta só o status `201`) |
| 6. `UsernameNotFoundException` | ✅ no `AuthorizationService` (restam os 400 no `UserService`) |
| Login com senha errada → 500 | ✅ handler de `AuthenticationException` → 401 (**testar na prática**) |

---

## 🔴 Bugs abertos (verificados no código atual)

### 4. ~~Escalada de privilégio no registro~~ ✅ RESOLVIDO (2026-08-15)
`UserRequestDto` sem `role`; `save()` força `Role.CREATOR`.
Pendências menores: (1) o import de `Role` ficou sem uso no `UserRequestDto`; (2) quem cria usuários VERIFIER/ADMIN? (ex.: endpoint exclusivo de ADMIN, ou seed no banco).

### 5. ~~Hash de senha vazando na resposta~~ ✅ RESOLVIDO (2026-08-15)
`UserResponseDto` + `UserMapper` — a senha não sai mais da API.
Pendência: status ainda é `200` — o RESTful correto para criação é `201 Created` (+ cabeçalho `Location`, opcional).

### 6. ~~`AuthorizationService` viola o contrato~~ ✅ RESOLVIDO (2026-08-15)
Agora lança `UsernameNotFoundException` — o `DaoAuthenticationProvider` trata isso escondendo a existência do usuário (anti-enumeração).
Pendência relacionada: `UserService.findByLogin` e `findByLoginDetails` ainda lançam `ResponseStatusException(400)` — se um token válido referenciar um usuário deletado, o filtro devolve 400. Resolver junto com o item 8.

### NOVO: ~~Login com senha errada devolve 500~~ ✅ RESOLVIDO (2026-08-15)
Handler de `AuthenticationException` → `401` adicionado ao `GlobalExceptionHandler` (verifiquei que `getAuthenticationRequest()` existe no Spring Security 7.1 do seu projeto — ok).
**Ação pendente:** teste de verdade — login com senha errada deve devolver 401 JSON.

### NOVO (CRÍTICO): O hash de integridade não funciona — o coração do domínio
`MediaMapper.toMedia()` faz:
```java
PasswordEncoder encoder = new PasswordEncoder();
media.setHash(encoder.encoder().encode(file.getBytes().toString()));
```
Três problemas conceituais aqui:
1. `file.getBytes().toString()` em um `byte[]` devolve `[B@<hashcode>` — a identidade do objeto em memória, **não o conteúdo do arquivo**. Dois uploads do mesmo arquivo geram "hashes" diferentes, e o hash não diz nada sobre os bytes.
2. **BCrypt é para senhas**, não para integridade de arquivo (tem sal aleatório, custo computacional e limite de ~72 bytes). Para integridade usa-se hash criptográfico como **SHA-256** (`MessageDigest`).
3. `new PasswordEncoder()` manual dentro do mapper quebra a injeção de dependência — você está instanciando uma `@Configuration` como se fosse utilitária. Hash não é responsabilidade do mapper.

Além disso: o hash nunca é **verificado** depois (não existe fluxo de re-cálculo e comparação), e `file.getBytes()` carrega o arquivo inteiro na memória.
Dica de caminho: método no service (ou classe própria) que recebe `MultipartFile`/bytes e devolve o SHA-256 hex.

### NOVO: Relacionamento circular Media ↔ Review
`Media` tem `@ManyToOne Review review` e `Review` tem `@ManyToOne Media media` — dois lados donos (duas FKs para a mesma relação). Combinado com `@Data`, o `toString()` de um chama o do outro infinitamente → `StackOverflowError` no primeiro log.
Dica: `@OneToOne(mappedBy = "media")` de um lado, ou repensar quem conhece quem.

### NOVO: `isActive` nunca é definido
`UserService.save()` não seta `isActive` → grava `NULL` no banco. Decida: valor default `true` no save, ou `@PrePersist`, ou remova o campo.

---

## 🟠 Segurança (abertas)

### 7. Segredo do JWT — AINDA ABERTO (e piorou)
- `.env` com o segredo real ainda existe no histórico da linha antiga do GitHub (o tree da linha de 24 commits incluía `.env`). **Rotacione o segredo** — apagar não adianta, o valor vazou para o histórico do repositório.
- `.gitignore` do repo atual **não inclui `.env`**.
- Crie `.env.example` com placeholder e commitado.

### 8. Token inválido vira 500 — AINDA ABERTO
- `SecurityFilter` não tem try/catch; `validateToken` lança `RuntimeException` → 500.
- `recoveryToken` não verifica o prefixo `"Bearer "` — header presente mas malformado também vira 500.
- Não há `AuthenticationEntryPoint` configurado → requisição sem token em rota protegida devolve 403 em vez de 401.
Padrão: token inválido → 401 JSON padronizado.

### 9. Segredo default fraco — AINDA ABERTO
`${JWT_SECRET:my-secret-key}` continua no `application.yaml`. Em produção, falhe rápido se não houver segredo.

---

## 🟡 Profissionalismo (abertas)

### 12. Swagger — AINDA ABERTO
README promete `/swagger-ui/index.html`; não há `springdoc-openapi` no pom. Adicione a dependência ou ajuste o README.

### 13. `@Data` nas entidades — AINDA ABERTO (agora pior)
`User`, `Media`, `Review`, `RequestReviewMedia` usam `@Data`. Com relacionamentos (incluindo o circular acima), `toString`/`equals` viram armadilhas. Recomendado: `@Getter`/`@Setter` + `equals`/`hashCode` explícitos.

### 15. Duplicação — AINDA ABERTO
`AuthorizationService.loadUserByUsername` e `UserService.findByLoginDetails` constroem o mesmo `UserDetails`. Um deve delegar ao outro.

### 17. Expiração e issuer hardcoded — AINDA ABERTO
30 min e `"nicholas pereira"` fixos no `TokenService`. Pesquise `@ConfigurationProperties`.

### 18. `ddl-auto: update` — AINDA ABERTO
Pesquise **Flyway** (ou Liquibase). Requisito em quase toda vaga Java.

### 19. Pom e imports — MAIORIA AINDA ABERTO
- Tags vazias do Initializr (`<name/>`, `<description/>`, `<licenses>`, `<developers>`, `<scm>`).
- `spring-boot-starter-security-oauth2-authorization-server` e `-resource-server` (+ seus starters de teste) não são usados — você usa `auth0/java-jwt`.
- `TokenService`: import não usado (`org.springframework.security.oauth2.jwt.Jwt`) e catch que re-lança a mesma exceção sem agregar nada.

### 20. Testes — AINDA ABERTO
Só `contextLoads`. O `instrucoes.md` pede testes de autenticação, autorização e regra de verificação. Comece: `register` (sucesso/duplicado) e `login` (sucesso/senha errada) com `MockMvc`.

### 21. Classe `config.PasswordEncoder` — AINDA ABERTO
Mesmo nome da interface do Spring. O `@Bean` deveria viver na `SecurityConfig`.

### Novos pontos de código

- **`Optional<List<Media>>` no repository** — `Optional` de coleção é anti-padrão: o Spring Data devolve lista vazia naturalmente. O `.get()` subsequente no service é cheiro.
- **`.get()` sem verificação** — `registerMedia` faz `userRepository.findByLogin(...).get()`. Use `orElseThrow`.
- **`@RequestBody String reason`** — string crua no `requestReview`; use DTO com validação.
- **`reviewMedia` não valida transições de status** — hoje dá para revisar uma mídia já VERIFIED/REJECTED, re-revisar, e o revisor pode ser o próprio dono. O `instrucoes.md` (decisão 8) pede transições permitidas explícitas.
- **4 endpoints quase idênticos** (`search/pending|rejected|verified`) — um endpoint com query param `?status=` eliminaria a duplicação.
- **`@Autowired` de campo na `SecurityConfig`** — você já usa injeção por construtor via Lombok nos outros lugares; mantenha o padrão.
- **Credenciais de banco hardcoded no `application.yaml`** — use variáveis de ambiente.
- **`listRequestReview` restrito a ADMIN/CREATOR** — um VERIFIER não deveria ver os pedidos de revisão? Decisão de domínio: decida e documente no README.

---

## ⚠️ Situação do git (atualizada)

**Boa notícia:** o repositório externo (`vca`) está **em sync com o GitHub** (`origin/main = 287b868`). Seu código atual está seguro e enviado. ✅

Pendências:
1. **O fix do bug 1 (SecurityFilter) não foi commitado** — `git status` mostra o arquivo modificado. Faça o commit com mensagem convencional (ex.: `fix: use HttpHeaders.AUTHORIZATION to recover JWT`).
2. **A pasta `VeriMedia API/` é um segundo clone do MESMO repositório** (`github.com/nickz9x/VeriMedia-API`), com refs desatualizadas, working tree vazio e um commit local órfão ("Initial commit"). É candidata a **exclusão** — antes, confirme que não há nada lá que você precise (esta revisão já foi movida para cá).
3. **`.gitattributes` está untracked** no repo externo — commit ou remova.
4. A linha antiga de 24 commits pode ter ido ao GitHub com o `.env` → **rotacione o JWT secret** independentemente.

---

## Ordem de ataque (atualizada)

1. Commit do fix do SecurityFilter + das correções novas (bugs 4, 5, 6, login 401) — estão todos no working tree.
2. Testar o login com senha errada (confirmar 401).
3. **Hash de integridade** — decidir onde o arquivo fica, implementar SHA-256 e o endpoint de verificação.
4. Item 8: token inválido (try/catch no filtro, prefixo "Bearer ", `AuthenticationEntryPoint`) + os 400 restantes no `UserService`.
5. Relacionamento circular Media↔Review + `@Data` nas entidades.
6. Git: destino da pasta `VeriMedia API/`, `.gitattributes`, rotacionar o segredo (`.env` no `.gitignore` + `.env.example`).
7. Migrations (Flyway), testes, springdoc, limpeza do pom, `201` no register.
8. Transições de status + decisões de domínio documentadas no README.

---

## Questões de autoverificação

- Por que `byte[].toString()` não representa o conteúdo do arquivo?
- Por que BCrypt não serve para hash de integridade de arquivo?
- O que acontece quando `Media.toString()` chama `Review.toString()`? Por quê?
- Por que `roles(...)` do `User.builder()` adiciona o prefixo `ROLE_` — e como isso se conecta ao `hasRole(...)` no `@PreAuthorize`?
