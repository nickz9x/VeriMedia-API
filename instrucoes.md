# VeriMedia API — Transparência de Conteúdo Criado com IA

## Desafio

Crie uma API REST que permita registrar a procedência de uma mídia digital. A proposta é que alguém consiga publicar uma imagem, vídeo, áudio ou documento e informar se ele foi criado por uma pessoa, gerado por IA ou modificado com IA.

Depois, outra pessoa deve poder consultar uma versão pública e entender o histórico básico daquele conteúdo.

O projeto se relaciona a um tema atual: transparência sobre mídias sintéticas, deepfakes e confiança no conteúdo digital.

> A aplicação não precisa detectar se algo é deepfake. O foco é registrar declarações, proteger a integridade do arquivo e criar um processo de validação.

## O que o sistema precisa fazer

- Usuários devem se autenticar.
- Um criador deve cadastrar uma mídia.
- Para cada mídia, o criador deve informar a origem do conteúdo.
- A API deve guardar uma forma de verificar se o arquivo foi alterado depois do cadastro.
- Deve existir um fluxo para um usuário autorizado validar ou questionar a declaração.
- Deve ser possível compartilhar uma consulta pública, sem expor dados privados.

## Perfis iniciais

- `CREATOR`: cadastra e gerencia as próprias mídias.
- `VERIFIER`: revisa as declarações submetidas.
- `ADMIN`: administra usuários e consulta informações da plataforma.
- `PUBLIC`: só consulta conteúdos compartilhados.

## Requisitos de domínio

- Uma mídia possui dono, tipo, arquivo e status.
- Uma mídia precisa ter uma declaração de origem.
- A declaração deve distinguir, ao menos:
    - criada por humano;
    - assistida por IA;
    - gerada por IA;
    - manipulada por IA.
- Mídias e declarações devem ter data de criação.
- O sistema deve registrar o resultado de uma verificação.
- O arquivo deve possuir um hash para permitir conferência de integridade.

## Rotas mínimas sugeridas

| Método | Rota | Ideia |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Criar usuário. |
| `POST` | `/api/auth/login` | Autenticar e obter JWT. |
| `POST` | `/api/media` | Cadastrar uma mídia. |
| `GET` | `/api/media/{id}` | Consultar uma mídia. |
| `POST` | `/api/media/{id}/declaration` | Informar como o conteúdo foi produzido. |
| `POST` | `/api/media/{id}/verification-request` | Pedir revisão. |
| `POST` | `/api/verification-requests/{id}/review` | Aprovar ou questionar a declaração. |
| `GET` | `/public/media/{token}` | Consultar uma mídia compartilhada. |

Você pode criar, remover ou mudar essas rotas se a sua modelagem pedir algo diferente.

## Requisitos técnicos

- Java com Spring Boot.
- Spring Data JPA para persistência.
- Spring Security com JWT e autorização por papel.
- Bean Validation nos DTOs de entrada.
- PostgreSQL ou outro banco relacional.
- OpenAPI/Swagger para documentar e testar a API.
- Tratamento global de erros.
- Testes para, pelo menos, autenticação, autorização e regra de verificação.

## Decisões para você explorar

Estas são propositalmente abertas. Documente as escolhas no README.

1. Uma mídia poderá ter várias versões? O que deve acontecer quando ela for modificada?
2. Quem pode criar um link público: só o criador ou também um administrador?
3. Qual informação pode aparecer na consulta pública sem ferir a privacidade do criador?
4. Como uma mídia passa de pendente para verificada? Pode haver mais de um revisor?
5. Uma declaração pode ser corrigida? Se sim, como preservar o histórico?
6. Onde o arquivo deve ficar no MVP: banco de dados, disco local ou storage externo?
7. O hash deve ser calculado quando e como deve ser usado para detectar adulteração?
8. Quais status fazem sentido para uma mídia e quais transições são permitidas?

## Para o README

Explique o problema que a API resolve, apresente as principais decisões acima, adicione um diagrama simples das entidades e mostre como executar a aplicação. Inclua a URL do Swagger e exemplos de requisição autenticada.

## Critério de conclusão

Considere o exercício concluído quando um criador consegue cadastrar uma mídia e sua declaração, um verificador consegue registrar um parecer e uma pessoa externa consegue consultar apenas os dados públicos por um link/token.
