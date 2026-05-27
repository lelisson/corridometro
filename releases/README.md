# Releases

Artefatos gerados localmente. **APKs e AABs não entram no Git** (ver `.gitignore`).

| Pasta | Uso |
|-------|-----|
| `VERSIONS.md` | Histórico de versões confirmadas |
| `oficial/` | APK debug + AAB oficial (`build-oficial.ps1`) |
| `playstore/` | AAB para upload na Play (gitignored) |
| `login/` | Variante `com.corridometro.login` |
| `paga/` | Referência legada |

## Gerar oficial

```powershell
.\scripts\build\build-oficial.ps1
```

Guia completo: [docs/guides/publishing/play-store.md](../docs/guides/publishing/play-store.md)
