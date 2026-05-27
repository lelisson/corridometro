# Scripts

Todos os scripts assumem PowerShell na **raiz do projeto** (`c:\corridometro`).

## Build

| Script | Descrição |
|--------|-----------|
| `build\build-apk.ps1` | APK debug oficial (`standardConceptA`) |
| `build\build-apk-flavor.ps1` | APK por edition + design |
| `build\build-play-bundle.ps1` | `.aab` assinado para Play Console |
| `build\build-oficial.ps1` | APK em `releases/oficial/` + AAB |
| `build\build-design-apks.ps1` | APKs conceito A e B para comparar |

## Release

| Script | Descrição |
|--------|-----------|
| `release\create-release-keystore.ps1` | Cria keystore de release (uma vez) |
| `release\archive-releases.ps1` | Arquiva APKs legados em `releases/` |

## Desenvolvimento

| Script | Descrição |
|--------|-----------|
| `dev\preview-app.ps1` | Instala e abre no emulador/USB |
| `dev\preview-web.ps1` | Servidor local `preview-web/` |
| `dev\start-emulator.ps1` | Inicia AVD e aguarda online |
| `dev\start-emulator-safe.ps1` | Emulador modo estável (GPU software) |

## Setup

| Script | Descrição |
|--------|-----------|
| `setup\setup-android-studio.ps1` | Copia Run configurations para `.idea/` |
| `setup\check-firebase-setup.ps1` | Valida `google-services.json` |
| `setup\update-launcher-icon.ps1` | Regenera ícones a partir do logo PNG |

## Atalho Windows

```bat
build-apk.bat
```

Equivale a `scripts\build\build-apk.ps1`.
