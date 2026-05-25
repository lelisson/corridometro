# Corridômetro

App Android para motoristas de aplicativo registrarem jornadas, ganhos, custos e acompanharem o lucro real por período e por plataforma.

## Visão geral

O Corridômetro foi pensado para uso prático no dia a dia de quem trabalha com apps de corrida e entrega. O foco é mostrar quanto realmente sobra depois dos custos, com uma interface simples e rápida para registrar jornadas e consultar relatórios.

## Principais recursos

- registro de jornadas com plataforma, horário, km, faturamento e despesas
- resumo de lucro líquido, faturamento, custos e `R$/km`
- filtros por período
- visão por aplicativo
- relatórios e histórico
- assinatura Premium via Google Play
- estrutura preparada para anúncios e integrações opcionais do Google

## Stack

- `Kotlin`
- `Jetpack Compose`
- `Room`
- `ViewModel` + `Flow`
- `Google Play Billing`
- `AdMob` (ativado somente quando IDs reais estiverem configurados)

## Executar o projeto

1. Instale o [Android Studio](https://developer.android.com/studio).
2. Abra a pasta `corridometro`.
3. Aguarde o Gradle Sync.
4. Rode no emulador ou em um dispositivo Android com depuração USB.

Build local principal:

```powershell
.\build-apk.ps1
```

Bundle oficial para Play Store:

```powershell
.\build-play-bundle.ps1
```

## Estrutura

```text
app/src/main/java/com/corridometro/
├── data/
├── domain/
├── ui/
└── util/
```

## Publicação

- pacote oficial: `com.corridometro`
- variante oficial: `standardConceptA`
- política de privacidade: `docs/privacy-policy.html`

## Segurança do repositório

Este projeto foi configurado para **não publicar** arquivos sensíveis comuns, como:

- `google-services.json`
- `keystore.properties`
- arquivos `.jks` e keystores
- builds `.apk` e `.aab`
- configurações locais do editor

Mesmo assim, antes de fazer o primeiro push, vale revisar com atenção tudo que estiver staged no `git`.

## Política de privacidade

Arquivo local:

`docs/privacy-policy.html`

Quando você publicar essa página no GitHub Pages, Google Sites ou outro host estático, use a URL pública na Play Console.

## Status

Projeto em desenvolvimento e preparação para publicação na Google Play Store.
