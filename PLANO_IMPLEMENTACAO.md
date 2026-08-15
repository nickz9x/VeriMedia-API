# Plano de Implementação — Hash de Integridade & Versionamento (append-only)

> Data: 2026-08-15
> Decisão do dono: versionamento "estilo git" — cada versão é imutável e permanentemente verificável pelo seu próprio `publicToken`.
> Complementa `REVISAO_PROFISSIONAL.md` (backlog de revisão).

---

## 1. Decisões de domínio (fechadas)

| # | Decisão | Justificativa |
|---|---|---|
| D1 | Mídia **imutável** após o registro | Hash só prova integridade se a referência for estável; sobrescrever arquivo + hash destrói a trilha de confiança |
| D2 | Modificação = **nova versão** (modelagem A: `version` + `parentMedia` self-reference) | Análogo a commits do git: nunca editar, sempre acrescentar |
| D3 | **Cada versão tem seu `publicToken`, estável e eterno** | Citações/evidências publicadas (ex.: matéria de jornalista) devem continuar verificáveis para sempre |
| D4 | A "última versão" é resolvida por endpoint autenticado (por id); token nunca é reapontado | Separa "endereço permanente" de "consulta atual" |
| D5 | Arquivo no disco: **uma pasta por mídia, nome gerado pelo servidor** (UUID), nome original só no banco (`mediaName`) | Evita path traversal, colisões e caracteres inválidos |
| D6 | **SHA-256** para integridade; comparação com `MessageDigest.isEqual` | Hash criptográfico determinístico; comparação em tempo constante |
| D7 | Verificação via endpoint **público** `POST` por `publicToken`; resposta `{ matches, mediaName, status }` | Consulta pública sem expor dados do criador |
| D8 | Diretório de storage configurável no `application.yaml`; volume no docker-compose; pasta `storage/` no `.gitignore` | Arquivos de mídia não vão para o git |

---

## 2. Etapas de implementação

### Etapa 1 — Entidade `Media` com versionamento

**O que fazer:**
- Adicionar `Integer version` e `@ManyToOne Media parentMedia` (null na raiz).
- Trocar `@Data` por `@Getter`/`@Setter` em `Media` (e nas demais entidades) — self-reference + `@Data` = `toString()` em loop infinito garantido.

**Critério de aceite:** registrar uma mídia cria `version = 1` e `parentMedia = null`; logar a entidade não estoura `StackOverflowError`.

### Etapa 2 — Hash real + gravação do arquivo no disco

**O que fazer:**
- Criar o cálculo SHA-256 **no service** (não no mapper) — `MessageDigest` + `HexFormat.of().formatHex(...)`.
- Substituir o trecho quebrado em `MediaMapper` (`byte[].toString()` + BCrypt + `new PasswordEncoder()`).
- Fluxo do registro: salvar `Media` → usar o id para criar a pasta (`storage/{id}/`) → gravar o arquivo com nome gerado (UUID + extensão) → atualizar a entidade.
- Diretório base do `application.yaml` (ex.: `app.storage.dir: ${STORAGE_DIR:./storage}`).
- `storage/` no `.gitignore`.

**Critério de aceite:** dois uploads com o mesmo conteúdo geram o mesmo hash; arquivos diferentes geram hashes diferentes; o arquivo aparece em disco com nome seguro.

**Armadilha conhecida:** se o arquivo falhar ao gravar depois do `save()`, fica registro órfão — MVP: aceitar e documentar; o ideal seria limpar no catch.

### Etapa 3 — Endpoint de nova versão

**O que fazer:**
- `POST /api/media/{id}/version` (multipart, autenticado, reutilizar `MediaRegisterRequestDto`).
- Regra de negócio: **só o dono** cria versão (comparar `authentication.getName()` com o dono da mídia).
- Resolver a raiz (caminhar `parentMedia`) e calcular `version = última + 1`.
- Nova versão: novo arquivo, novo hash, novo `publicToken`, `status = PENDING` — a antiga permanece intacta.

**Critério de aceite:** dono cria v2 sem tocar na v1; não-dono recebe 403; v1 continua verificável.

### Etapa 4 — Endpoint público de verificação

**O que fazer:**
- `POST /api/media/public/verify/{publicToken}` (multipart).
- Recalcular SHA-256 do arquivo recebido e comparar com o armazenado via `MessageDigest.isEqual`.
- Resposta: `{ matches, mediaName, status }`.
- ⚠️ **Integração com a segurança:** adicionar o caminho ao `requestMatchers` de permitAll do `SecurityConfig` (hoje só `/api/media/public/search/**` é público).

**Critério de aceite:** arquivo original → `matches = true`; arquivo com 1 byte alterado → `matches = false`; token inexistente → 404 (via `GlobalExceptionHandler`).

### Etapa 5 — Ajustes finais da feature

**O que fazer:**
- `PublicMediaResponse`: considerar incluir `version` e `hash` (dados públicos, aumentam o valor de auditoria).
- Docker: volume para o storage no `docker-compose.yaml`.
- README: documentar as decisões D1–D8 (o `instrucoes.md` exige) e atualizar a tabela de rotas com `/version` e `/verify`.

### Etapa 6 — Testes da feature

**O que fazer:**
- Unitário do hash: determinístico (mesmos bytes → mesmo hash; bytes diferentes → hash diferente).
- Integração: registrar → verificar com o mesmo arquivo → `true`; com arquivo adulterado → `false`.
- Autorização: não-dono tentando criar versão → 403.
- Sucesso do fluxo completo: register → version → verify da v1 (continua funcionando!) e da v2.

---

## 3. Pontos de melhoria pendentes (consolidado)

> Detalhes em `REVISAO_PROFISSIONAL.md`. Prioridade sugerida:

1. **Item 8 — token inválido vira 500:** try/catch no `SecurityFilter`, verificação do prefixo `"Bearer "`, `AuthenticationEntryPoint` para 401. (Os `ResponseStatusException(400)` do `UserService` entram aqui.)
2. **Relacionamento circular Media↔Review:** dois `@ManyToOne` donos — `@OneToOne(mappedBy=...)` de um lado. (Parte coberta na Etapa 1 se trocar `@Data`.)
3. **`201 Created` no register** (hoje 200).
4. **Segredos:** rotacionar o JWT secret (vazou no histórico antigo do GitHub), `.env` no `.gitignore`, criar `.env.example`, remover o default fraco do yaml.
5. **Git:** commit das correções atuais (SecurityFilter, bugs 4/5/6, login 401); decidir destino da pasta `VeriMedia API/`; `.gitattributes`.
6. **Flyway** no lugar de `ddl-auto: update` — faça **antes** de qualquer mudança de schema em produção.
7. **springdoc-openapi** (README promete Swagger) + limpeza do pom (deps OAuth2 não usadas, tags vazias, imports).
8. **Transições de status** no `reviewMedia` (não revisar mídia já VERIFIED/REJECTED; revisor ≠ dono) + decisões documentadas.

---

## 4. Ordem geral de trabalho

| Fase | Atividade | Entrega |
|---|---|---|
| 0 | Commit das correções atuais (bugs 4/5/6 + login 401) | Working tree limpo |
| 1 | Etapas 1–2 (entidade + hash + storage) | Registro com integridade real |
| 2 | Etapas 3–4 (versão + verificação) | Feature completa de ponta a ponta |
| 3 | Etapa 5–6 (ajustes + testes) | Feature testada e documentada |
| 4 | Melhorias 1–3 (segurança do filtro, circular, 201) | API sólida |
| 5 | Melhorias 4–8 (secrets, git, Flyway, springdoc, status) | Projeto "profissional" |
