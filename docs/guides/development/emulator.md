# Emulador Android

## Início normal

```powershell
.\scripts\dev\start-emulator.ps1
```

Aguarde **Emulador ONLINE** antes do Run no Android Studio.

## Modo estável (recomendado se o AVD “terminated”)

```powershell
.\scripts\dev\start-emulator-safe.ps1
```

Usa GPU software e não carrega snapshot — mais lento, porém mais estável.

## Criar um AVD

**Android Studio → Tools → Device Manager → Create Device**

- Dispositivo: Pixel 6 (ou similar)
- Imagem: API 34 ou 35 com **Google Play**
- Evite imagens “Google APIs” sem Play se for testar Billing

## Run no Android Studio

1. Emulador online
2. **Corridometro (oficial)** → Run
3. Se falhar: feche o emulador, `start-emulator-safe.ps1`, tente de novo

## Celular físico

Ative **Depuração USB**, conecte o cabo e use:

```powershell
.\scripts\dev\preview-app.ps1
```
