# App interativo no Android Studio

## Configuração inicial (uma vez)

```powershell
.\scripts\setup\setup-android-studio.ps1
```

Isso instala em `.idea/runConfigurations/`:

- **Corridometro (oficial)** → `standardConceptADebug`
- **Corridometro (login)** → `loginConceptADebug`

## Abrir o projeto

1. **File → Open** → pasta `corridometro`
2. Aguarde **Gradle Sync**

## Emulador

1. **Tools → Device Manager**
2. **Create Device** → Pixel 6, API 34+ (Google Play)
3. Inicie o AVD antes do Run

Ou no terminal:

```powershell
.\scripts\dev\start-emulator.ps1
```

Se o emulador fechar sozinho ou der timeout no Run, use [emulator.md](emulator.md) ou `start-emulator-safe.ps1`.

## Rodar

1. Emulador **já online**
2. Barra superior → **Corridometro (oficial)** → Run (▶)

## Variantes

| Configuração | Pacote | Uso |
|--------------|--------|-----|
| Corridometro (oficial) | `com.corridometro` | Play Store, uso normal |
| Corridometro (login) | `com.corridometro.login` | App separado com login obrigatório |

## Problemas comuns

| Sintoma | Ação |
|---------|------|
| Timeout 300s no deploy | Emulador ligado antes do Run; ver [emulator.md](emulator.md) |
| `google-services.json` ausente | Opcional para UI local; ver [firebase.md](../integrations/firebase.md) |
| Gradle Sync falha | **File → Invalidate Caches** ou abrir com JDK do Android Studio |
