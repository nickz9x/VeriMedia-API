# TODO — VeriMedia API

> Atualizado: 2026-08-15
> Fontes: `PLANO_IMPLEMENTACAO.md` (detalhes da feature) e `REVISAO_PROFISSIONAL.md` (detalhes das correções)

---

## Fase 0 — Sync de trabalho ✅ CONCLUÍDA (2026-08-15)

- [x] Commit das correções atuais (3 commits atômicos: token, registro, 401)
- [x] Commit dos documentos de plano e revisão
- [x] Push para o GitHub

---

## Fase 1 — Feature: hash de integridade + versionamento

- [ ] **Etapa 1:** `Media.version` + `Media.parentMedia`; trocar `@Data` por `@Getter`/`@Setter` nas entidades — **EM ANDAMENTO** (verificação 2026-08-15; ainda não commitado)
  - [x] `Media`: `version` + `parentMedia` adicionados; `@Data` → `@Getter`/`@Setter`
  - [ ] `User`, `Review`, `RequestReviewMedia` ainda usam `@Data`
  - [x] `registerMedia` seta `version = 1`
- [ ] **Etapa 2:** SHA-256 no service (substituir trecho quebrado do `MediaMapper`); arquivo no disco (pasta por mídia, nome UUID); diretório base no `application.yaml`; `storage/` no `.gitignore`
- [ ] **Etapa 3:** `POST /api/media/{id}/version` — só o dono; versão = última + 1; v1 intacta — **EM ANDAMENTO** (rascunho do `newVersion` no `MediaService`; falta: id da mídia existente, checagem real do dono, cálculo da última versão, return)
- [ ] **Etapa 4:** `POST /api/media/public/verify/{publicToken}` → `{ matches, mediaName, status }`; adicionar caminho ao permitAll do `SecurityConfig`
- [ ] **Etapa 5:** `PublicMediaResponse` com `version` + `hash`; volume do storage no docker-compose; README atualizado (decisões D1–D8 + rotas novas)
- [ ] **Etapa 6:** Testes — hash determinístico, match/mismatch, 403 para não-dono, fluxo completo register → version → verify (v1 continua funcionando)

## Fase 2 — Segurança residual

- [ ] Token inválido → 401: try/catch no `SecurityFilter`, verificação do prefixo `"Bearer "`, `AuthenticationEntryPoint`; trocar os 400 restantes do `UserService`
- [ ] Testar login com senha errada → confirmar 401 na prática
- [ ] Relacionamento circular Media↔Review (`@OneToOne(mappedBy=...)` de um lado)
- [ ] `201 Created` no register (hoje 200)

## Fase 3 — Segredos e git

- [ ] Rotacionar o JWT secret (vazou no histórico antigo do GitHub)
- [ ] `.env` no `.gitignore` + criar `.env.example` + remover default fraco do yaml
- [ ] Decidir destino da pasta `VeriMedia API/` (recomendado: excluir) e do `.gitattributes` (recomendado: commitar)

## Fase 4 — Infra e qualidade

- [ ] Flyway no lugar de `ddl-auto: update`
- [ ] springdoc-openapi + limpeza do pom (deps OAuth2 não usadas, tags vazias, imports)
- [ ] Transições de status no `reviewMedia` (não revisar mídia já finalizada; revisor ≠ dono)
- [ ] Decisões de domínio documentadas no README (exigência do `instrucoes.md`)

---

## Regra de ouro dos commits

- Um commit = uma unidade coerente e reversível
- Sujeito no imperativo, minúsculo, sem ponto final: `fix: ...`, `feat: ...`, `docs: ...`
- Ao terminar uma fase: `git push` (o desktop precisa de `git pull` para acompanhar)
