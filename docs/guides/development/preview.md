# Visualizar o app antes de versionar

## 1. Preview no Android Studio (mais rápido)

1. Abra `app/src/main/java/com/corridometro/ui/preview/ScreenPreviews.kt`
2. **View → Tool Windows → Preview** (ou Split)
3. **Build & Refresh** se necessário

Dados fictícios em `PreviewSampleData.kt`.

## 2. App interativo (emulador)

1. Uma vez: `.\scripts\setup\setup-android-studio.ps1`
2. Android Studio → Open → pasta do projeto → Gradle Sync
3. Device Manager → iniciar emulador (ou `.\scripts\dev\start-emulator.ps1`)
4. Run → **Corridometro (oficial)**

Guia detalhado: [android-studio.md](android-studio.md)

## 3. Celular ou emulador via script

```powershell
.\scripts\dev\preview-app.ps1
```

## 4. Só compilar APK

```powershell
.\scripts\build\build-apk.ps1
```

## 5. Dois designs (A + B)

```powershell
.\scripts\build\build-design-apks.ps1
```

Saída: `app/designs/output/`. Detalhes em `app/designs/README.md`.

## Fluxo antes de salvar versão

1. Conferir visual (Preview ou `preview-app.ps1`)
2. Confirmar se a mudança merece versão (`releases/VERSIONS.md`)
3. Só então aumentar `versionCode` / gerar APK ou AAB
