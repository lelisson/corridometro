# Segurança do repositório

Este repositório é **público**. Nunca commite:

| Arquivo | Motivo |
|---------|--------|
| `app/google-services.json` | credenciais Firebase |
| `keystore.properties` | senhas da keystore de release (modelo: `config/keystore.properties.example`) |
| `keystore/` ou `*.jks` | chave de assinatura do app na Play |
| `*.apk`, `*.aab` em `releases/` | artefatos de build (gerar localmente) |
| `.cursor/`, `.claude/` | configuração local do editor |

O app na Play Store (`com.corridometro`) depende da **mesma keystore** em todos os uploads. Se a chave vazar, terceiros podem publicar atualizações em seu nome.

IDs de teste do AdMob no código são aceitáveis no repositório; troque pelos IDs reais apenas no `strings.xml` local ou em variante de release privada, conforme sua estratégia de publicação.
