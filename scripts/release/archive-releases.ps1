# Arquiva APKs de variantes legadas (standard/login) em releases/.
# Uso: .\scripts\release\archive-releases.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
Set-Location $root

$buildFlavor = Join-Path $PSScriptRoot "..\build\build-apk-flavor.ps1"

function Archive-Apk($Flavor, $Design, $DestDir, $FileName) {
    $apk = & $buildFlavor -Flavor $Flavor -Design $Design
    if (-not (Test-Path $DestDir)) {
        New-Item -ItemType Directory -Path $DestDir | Out-Null
    }
    $dest = Join-Path $DestDir $FileName
    Copy-Item -Path $apk -Destination $dest -Force
    Write-Host "Arquivado: $dest" -ForegroundColor Green
}

Write-Host "Arquivando variantes legadas (ajuste nomes em archive-releases.ps1 se necessario)..." -ForegroundColor Cyan
Archive-Apk "standard" "conceptA" (Join-Path $root "releases\paga") "corridometro-v2.0.0-7-versao-paga-debug.apk"
Archive-Apk "login" "conceptA" (Join-Path $root "releases\login") "corridometro-v1.4.1-com-login-6-debug.apk"

Write-Host ""
Write-Host "Concluido. Ver releases/paga/ e releases/login/" -ForegroundColor Cyan
