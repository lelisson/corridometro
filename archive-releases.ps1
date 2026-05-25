# Arquiva APKs standard e login nas pastas releases/ e releases/login/
# Uso: .\archive-releases.ps1

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot

function Archive-Apk($Flavor, $DestDir, $FileName) {
    $apk = & (Join-Path $root "build-apk-flavor.ps1") -Flavor $Flavor
    if (-not (Test-Path $DestDir)) {
        New-Item -ItemType Directory -Path $DestDir | Out-Null
    }
    $dest = Join-Path $DestDir $FileName
    Copy-Item -Path $apk -Destination $dest -Force
    Write-Host "Arquivado: $dest" -ForegroundColor Green
}

Archive-Apk "standard" (Join-Path $root "releases\paga") "corridometro-v2.0.0-7-versao-paga-debug.apk"
Archive-Apk "login" (Join-Path $root "releases\login") "corridometro-v1.4.1-com-login-6-debug.apk"

Write-Host ""
Write-Host "Concluido. Paga em releases/paga/ | Login em releases/login/" -ForegroundColor Cyan
