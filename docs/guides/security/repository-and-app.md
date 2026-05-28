# Segurança — repositório e app

Resumo para reduzir informação útil a atacantes e endurecer o app.

## Repositório (GitHub público)

| Faça | Evite |
|------|--------|
| Manter segredos só localmente | E-mail pessoal em Markdown/HTML |
| Usar `SECURITY.md` antes do push | Caminhos `C:\Users\SeuNome\...` em comentários |
| Política de privacidade sem e-mail fixo no Git | Colar `google-services.json` “só para testar” |

Contato legal: configure o e-mail na **Play Console**; a política em `docs/privacy-policy.html` remete à ficha da loja.

## App instalado

- Dados financeiros ficam no SQLite local; backup automático está **desligado**.
- Comunicação com Google/Firebase/AdMob só via **HTTPS**.
- Variante com login: só sincroniza com Firebase após autenticação.

## Firebase (se usar sync)

1. Cole `firebase/firestore.rules` no Console e publique.
2. Verifique Authentication → Google ativo.
3. Adicione SHA-1 de debug e release em Project Settings.
4. Rode localmente: `.\scripts\setup\check-firebase-setup.ps1` (não envia dados à internet).

## Se suspeitar de vazamento

1. Rotacione senha da keystore **não é possível** — proteja o `.jks` com cópia offline.
2. Regenere chaves API no Firebase/Google Cloud se `google-services.json` vazou.
3. Revise regras do Firestore e logs de uso no Console.

Detalhes: [SECURITY.md](../../../SECURITY.md) na raiz do projeto.
