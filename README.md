# Corridômetro

App Android para motoristas de aplicativo registrarem jornadas, ganhos, custos e acompanhar o **lucro líquido** por período e por plataforma.

[![Repositório](https://img.shields.io/github/stars/lelisson/corridometro?style=social)](https://github.com/lelisson/corridometro)

## Status

- **Play Console:** `com.corridometro` (variante oficial `standardConceptA`)
- **Privacidade:** https://lelisson.github.io/corridometro/privacy-policy.html
- **Repositório:** público — leia [SECURITY.md](SECURITY.md) antes de qualquer commit

## Recursos

- Jornadas com plataforma, horário, km, faturamento e despesas
- Resumo de lucro, faturamento, custos e R$/km
- Filtros por período e visão por app
- Relatórios e histórico
- Assinatura Premium (Google Play Billing)
- AdMob quando IDs reais estiverem em `strings.xml`

## Stack

Kotlin · Jetpack Compose · Room · ViewModel + Flow · Play Billing

## Começar

1. [Android Studio](https://developer.android.com/studio) — abrir a pasta do projeto
2. Gradle Sync
3. Emulador ou dispositivo USB com depuração

**Preview rápido (sem instalar):** `app/.../ui/preview/ScreenPreviews.kt` → aba Preview

**App interativo:** [docs/guides/development/android-studio.md](docs/guides/development/android-studio.md)

## Build

```powershell
# APK debug oficial
.\scripts\build\build-apk.ps1

# Bundle para Play Store (requer keystore.properties)
.\scripts\build\build-play-bundle.ps1
```

Atalho Windows: `build-apk.bat`

Mais scripts: [scripts/README.md](scripts/README.md)

## Estrutura do repositório

```text
corridometro/
├── app/                 # Código Android + designs/logo
├── docs/                # Política de privacidade e guias
├── firebase/            # Regras Firestore
├── config/              # Exemplos (keystore, Run do Android Studio)
├── scripts/             # Build, dev, release, setup
├── releases/            # Histórico VERSIONS.md (APKs locais)
├── preview-web/         # Mockups HTML
└── SECURITY.md          # O que nunca commitar
```

Código principal: `app/src/main/java/com/corridometro/`

## Publicação

| Item | Valor |
|------|--------|
| Pacote | `com.corridometro` |
| Variante | `standardConceptA` |
| Guia | [docs/guides/publishing/play-store.md](docs/guides/publishing/play-store.md) |

## Documentação

Índice completo: [docs/README.md](docs/README.md)

## Segurança

Nunca commite `google-services.json`, `keystore.properties`, `*.jks` ou APKs/AABs. Detalhes em [SECURITY.md](SECURITY.md).

## Licença

Código do autor. Uso e distribuição conforme política do repositório no GitHub.
