# Publicar na Google Play Store

**Conta:** devlelissoncampos@gmail.com  
**App oficial:** `standard` + `conceptA` → pacote `com.corridometro`  
**Versão no código:** ver `versionCode` / `versionName` em `app/build.gradle.kts`

A Play exige **`.aab` assinado** — não envie APK debug.

## A. Preparar no PC

### 1. Keystore (só na primeira vez)

```powershell
.\scripts\release\create-release-keystore.ps1
```

Guarde `keystore/` e senhas em local seguro. Perder a chave impede atualizar o mesmo app na Play.

Modelo de propriedades: `config/keystore.properties.example` → copiar para `keystore.properties` na raiz.

### 2. Firebase (opcional)

`app/google-services.json` — ver [firebase.md](../integrations/firebase.md)

### 3. Assinatura Premium

Ver [play-billing.md](play-billing.md)

### 4. AdMob

Substitua em `app/src/main/res/values/strings.xml`:

- `admob_app_id`
- `admob_banner_unit_id`
- `admob_interstitial_unit_id`

Enquanto forem IDs de teste do Google, anúncios permanecem desativados no código.

### 5. Gerar pacote

```powershell
.\scripts\build\build-oficial.ps1
```

Ou só o bundle:

```powershell
.\scripts\build\build-play-bundle.ps1
```

**Upload:** `releases/playstore/corridometro-vX.Y.Z-N-playstore.aab`

### 6. SHA-1 / SHA-256 (Firebase)

```powershell
.\gradlew.bat signingReport
```

Use os valores de `standardConceptARelease` no Firebase Console.

## B. Play Console

1. Criar app → idioma PT-BR → gratuito (com compras e anúncios)
2. **ID do pacote:** `com.corridometro`
3. Ficha da loja: ícone 512, feature graphic 1024×500, ≥2 screenshots
4. **Política de privacidade:** https://lelisson.github.io/corridometro/privacy-policy.html
5. Conteúdo do app: classificação, anúncios **sim**, compras **sim**
6. **Teste interno/fechado** → enviar `.aab` → testadores via link da Play
7. **Produção** após validar Premium, sync e anúncios

## C. Atualizações futuras

1. Aumentar `versionCode` e `versionName`
2. `.\scripts\build\build-play-bundle.ps1`
3. Nova versão na Console com o **mesmo** `corridometro-release.jks`

## Pastas de release

| Pasta | Conteúdo |
|-------|----------|
| `releases/oficial/` | APK debug + cópia do AAB oficial |
| `releases/playstore/` | AAB para upload (gitignored) |
| `releases/login/` | Variante `com.corridometro.login` (app separado) |

## Problemas comuns

| Erro | Solução |
|------|---------|
| Pedem APK ou Bundle | Envie `.aab`, não debug APK |
| Premium não abre | App instalado via teste Play + produtos criados |
| Login/sync falha | `google-services.json` + SHA release no Firebase |
| Privacidade rejeitada | URL pública da política (link acima) |
