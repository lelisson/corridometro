# Firebase — variante login (`com.corridometro.login`)

App separado que **exige** login Google. Checklist na tela até o Firebase estar OK.

## Validar no PC

```powershell
.\scripts\setup\check-firebase-setup.ps1
```

## Passos

1. Criar projeto no [Firebase Console](https://console.firebase.google.com/)
2. Registrar Android com pacote **`com.corridometro.login`** (pode incluir também `com.corridometro` no mesmo JSON)
3. `google-services.json` em `app/` (gitignored)
4. **Authentication → Google** — ativar
5. **Firestore** — criar banco
6. Regras — conteúdo de [`firebase/firestore.rules`](../../../firebase/firestore.rules)
7. **SHA-1** do debug/release (`signingReport`) no Firebase
8. Build:

```powershell
.\scripts\build\build-apk-flavor.ps1 login conceptA
```

## Build / instalar

```powershell
.\scripts\dev\preview-app.ps1 -Flavor login
```

Histórico de APKs: `releases/login/VERSIONS.md`
