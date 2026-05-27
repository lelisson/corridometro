# Assinatura Premium (Google Play Billing)

Três assinaturas — crie na Play Console com estes IDs exatos:

| ID | Preço sugerido |
|----|----------------|
| `corridometro_premium_mensal` | R$ 20/mês |
| `corridometro_premium_semestral` | R$ 100/6 meses |
| `corridometro_premium_anual` | R$ 200/ano |

Os preços reais vêm da loja; o app mostra fallbacks até a sincronização.

## Play Console

1. [play.google.com/console](https://play.google.com/console)
2. **Monetização → Produtos → Assinaturas**
3. Criar cada ID, ativar planos, salvar (propagação pode levar horas)

## Testar

- **Configuração → Teste de licença** — e-mails de teste
- Instalar via **teste interno/fechado** na Play (recomendado) ou build assinado com a mesma chave

Compras de teste não cobram de verdade.

## AdMob

1. [admob.google.com](https://admob.google.com/) — app `com.corridometro`
2. IDs reais em `strings.xml` (ver [play-store.md](play-store.md))

## Build

```powershell
.\scripts\build\build-apk.ps1
```

## No app

**Início → Seja Premium → Assinar** (conta Google da Play).  
**Restaurar assinatura** ao trocar de celular.

## Problemas

| Sintoma | Causa provável |
|---------|----------------|
| Plano indisponível | ID diferente ou assinatura inativa |
| Compra não abre | APK sideload sem teste Play |
| Premium não ativa | Aguardar confirmação; Restaurar assinatura |
