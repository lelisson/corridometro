# Instala configuracoes de Run no Android Studio (app interativo no emulador)
# Uso: .\setup-android-studio.ps1
# Depois: abra o projeto no Android Studio e use Run (Corridometro oficial)

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
$src = Join-Path $root "config\android-studio\runConfigurations"
$dest = Join-Path $root ".idea\runConfigurations"

if (-not (Test-Path $src)) {
    Write-Error "Pasta nao encontrada: $src"
}

New-Item -ItemType Directory -Force -Path $dest | Out-Null
Get-ChildItem $src -Filter "*.xml" | ForEach-Object {
    Copy-Item -Force $_.FullName (Join-Path $dest $_.Name)
}

Write-Host "Configuracoes de execucao instaladas em .idea/runConfigurations/" -ForegroundColor Green
Write-Host ""
Write-Host "Proximos passos no Android Studio:" -ForegroundColor Cyan
Write-Host "  1. File > Open > $root"
Write-Host "  2. Aguarde Gradle Sync terminar"
Write-Host "  3. Tools > Device Manager > crie/inicie um emulador (Play)"
Write-Host "  4. Barra superior: selecione 'Corridometro (oficial)' e clique Run (triangulo verde)"
Write-Host ""
Write-Host "Guia completo: ANDROID_STUDIO_INTERATIVO.txt"
