VERSAO OFICIAL — Corridometro (com.corridometro)
================================================

Pasta de referencia para publicacao e distribuicao da build standard.

Arquivos esperados:
  corridometro-v{versionName}-{versionCode}-oficial-debug.apk  — instalacao direta / arquivo
  corridometro-v{versionName}-{versionCode}-playstore.aab        — upload na Play Console

Gerar tudo:
  .\build-oficial.ps1

Somente APK debug:
  .\build-apk-flavor.ps1 standard
  (copiar manualmente ou usar build-oficial.ps1)

Play Console:
  Envie o .aab (NAO o APK debug).
  Guia: PLAY_STORE_PUBLISH.txt na raiz do projeto.

Historico: VERSIONS.md nesta pasta.
