# Copia Run configurations para .idea/ (local, nao versionado).
# Uso: .\scripts\setup\setup-android-studio.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
$src = Join-Path $root "config\android-studio\runConfigurations"
$dest = Join-Path $root ".idea\runConfigurations"

if (-not (Test-Path $src)) {
    Write-Error "Pasta nao encontrada: $src"
}

New-Item -ItemType Directory -Force -Path $dest | Out-Null
Get-ChildItem $src -Filter "*.xml" | ForEach-Object {
    Copy-Item -Force $_.FullName (Join-Path $dest $_.Name)
}

Write-Host "Run configurations instaladas em .idea/runConfigurations/" -ForegroundColor Green
Write-Host "Guia: docs/guides/development/android-studio.md"
