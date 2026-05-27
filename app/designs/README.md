# Variantes de design (mockups gerados)

Dois APKs que reproduzem as **imagens de referência** enviadas no início do projeto — não versões “técnicas” misturadas.

| APK | Referência | O que você vê |
|-----|------------|----------------|
| **Corridômetro A** (ícone verde) | `reference/mockup-inicio.png` | Barra com logo, card de lucro + gráfico, Hoje/7d/30d/Tudo, 3 métricas com ícone, lista Por aplicativo (Uber/99) |
| **Corridômetro B** (ícone azul) | `reference/mockup-jornada.png` | Título Jornada, blocos 1·2·3, cards Uber/99/inDrive, prévia R$ e grid 2×2, Salvar jornada, nav Resumo/Jornada/Relatórios/Perfil |

## Gerar e instalar

```powershell
.\build-design-apks.ps1
```

Saída em `app/designs/output/` e cópias em `releases/`.

- `com.corridometro` — app oficial (design A)  
- `com.corridometro.design.b` — mockup Jornada para comparar lado a lado  

## Código

| Mockup | Pacote Kotlin |
|--------|----------------|
| Início | `ui/mockup/MockupInicioScreen.kt` |
| Jornada | `ui/mockup/MockupJornadaScreen.kt` + `MockupBJornadaNavHost.kt` |

Flavor Gradle: `conceptA` → `MOCKUP_VARIANT=INICIO`, `conceptB` → `MOCKUP_VARIANT=JORNADA`.

## Logo do app

- Original (v2, minimal): `app/designs/logo/corridometro-logo.png`
- Fonte gerada: `assets/corridometro-logo-v2.png` (na pasta do projeto Cursor)
- Play Store (512×512): `app/designs/logo/corridometro-logo-playstore-512.png`
- Ícone instalado: `app/src/main/res/mipmap-*` + `drawable-nodpi/ic_launcher_foreground.png` (adaptive icon, fundo `#16A34A`).
- Conceito: dois círculos iguais — pneu + velocímetro, flat, verde marca.
