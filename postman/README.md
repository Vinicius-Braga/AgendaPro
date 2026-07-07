# Postman — Agenda Pro API

Arquivos:

- `AgendaPro.postman_collection.json` — todas as requisições da API.
- `AgendaPro-Local.postman_environment.json` — variáveis para ambiente local.

## Importar

No Postman: **Import** → selecione os dois arquivos.
Depois, no canto superior direito, selecione o environment **Agenda Pro - Local**.

## Fluxo recomendado

1. **Auth → Register** — cria um usuário.
2. **Auth → Login** — o `token` JWT é salvo automaticamente na variável `{{token}}`.
3. As rotas protegidas (Clients, Services, Appointments) já enviam
   `Authorization: Bearer {{token}}` herdado da collection — não precisa fazer nada.

Nenhuma requisição pede `userId`: o usuário é identificado pelo próprio token
(`@AuthenticationPrincipal` no backend), então clientes, serviços e
agendamentos criados ficam automaticamente vinculados a quem está logado.

## Como o token é armazenado automaticamente

A collection usa **Bearer Token** no nível raiz apontando para `{{token}}`.
A requisição **Login** tem um script na aba *Tests* que lê o campo `token`
da resposta e o grava na variável da collection:

```javascript
const body = pm.response.json();
pm.collectionVariables.set("token", body.token);
```

As rotas de `/auth` estão marcadas como **No Auth** (são públicas).

## Variáveis capturadas automaticamente

| Variável     | Preenchida por            |
|--------------|---------------------------|
| `token`      | Auth → Login              |
| `clientId`   | Clients → Create client   |
| `serviceId`  | Services → Create service |

Isso permite encadear as requisições sem copiar/colar IDs manualmente.
