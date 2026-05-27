# Firebase (app oficial `com.corridometro`)

Dados locais hoje: `/data/data/com.corridometro/databases/corridometro.db`

Para sync na nuvem com conta Google:

## Configuração

1. [console.firebase.google.com](https://console.firebase.google.com/) — criar projeto
2. Adicionar app Android → pacote **`com.corridometro`**
3. Baixar `google-services.json` → `app/google-services.json` (não commitar)
4. Ativar **Authentication → Google**
5. Criar **Firestore**
6. Regras: copiar de [`firebase/firestore.rules`](../../../firebase/firestore.rules) e publicar
7. Gradle Sync → `.\scripts\build\build-apk.ps1`

## Dados na nuvem

Após login: `users/{uid}/work_shifts`, `users/{uid}/expenses`

## SHA-1

Play release:

```powershell
.\gradlew.bat signingReport
```

Adicionar SHA-1/256 em **Project Settings → seu app Android**.

## Validar localmente

```powershell
.\scripts\setup\check-firebase-setup.ps1
```

Variante com login obrigatório: [firebase-login.md](firebase-login.md)
