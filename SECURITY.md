# Segurança do repositório

Este repositório é **público**. Nunca commite:

| Arquivo | Motivo |
|---------|--------|
| `app/google-services.json` | credenciais Firebase |
| `keystore.properties` | senhas da keystore de release (modelo: `config/keystore.properties.example`) |
| `keystore/` ou `*.jks` | chave de assinatura do app na Play |
| `*.apk`, `*.aab` em `releases/` | artefatos de build (gerar localmente) |
| `.cursor/`, `.claude/` | configuração local do editor |

O app na Play Store (`com.corridometro`) depende da **mesma keystore** em todos os uploads. Se a chave vazar, terceiros podem publicar atualizações em seu nome.

## O que não colocar na documentação pública

- E-mail pessoal ou senha da conta Play / Firebase
- IDs reais do AdMob (use só localmente; no Git ficam IDs de teste do Google)
- Links internos com tokens ou chaves de API
- Caminhos absolutos do seu PC (`C:\Users\...`)

Contato com usuários: use o e-mail configurado **somente na Play Console**, não no código-fonte.

## App (proteções no APK)

- Backup Android desativado (`allowBackup=false`)
- Tráfego HTTP em claro bloqueado (`network_security_config`)
- Dados locais excluídos de backup/transferência entre dispositivos (`data_extraction_rules`)
- Firestore: regras em `firebase/firestore.rules` — publique no Console e evite modo teste aberto

## Firebase / Google Cloud

1. Publicar regras de `firebase/firestore.rules` (negar raiz; permitir só `users/{uid}/...`).
2. Restringir OAuth Android por **SHA-1** e pacote `com.corridometro`.
3. Não deixar regras temporárias `if true` em produção.
4. Opcional: [Firebase App Check](https://firebase.google.com/docs/app-check) para reduzir abuso da API.

## Antes de cada push

```powershell
git status
git diff --staged
```

Confirme que não entrou `google-services.json`, `keystore.properties` ou `*.jks`.
