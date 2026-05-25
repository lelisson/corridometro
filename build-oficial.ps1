# Build oficial: APK debug em releases/oficial/ + AAB release para Play (standard + conceptA)
# Uso: .\build-oficial.ps1

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
Set-Location $root

function Get-AppVersion {
    $gradle = Get-Content (Join-Path $root "app\build.gradle.kts") -Raw
    if ($gradle -notmatch '(?s)defaultConfig\s*\{[^}]*versionCode\s*=\s*(\d+)[^}]*versionName\s*=\s*"([^"]+)"') {
        Write-Error "Nao foi possivel ler versionCode/versionName de app/build.gradle.kts"
    }
    return @{ Code = $matches[1]; Name = $matches[2] }
}

$ver = Get-AppVersion
$tag = "v$($ver.Name)-$($ver.Code)"
$oficialDir = Join-Path $root "releases\oficial"
$playDir = Join-Path $root "releases\playstore"
New-Item -ItemType Directory -Force -Path $oficialDir, $playDir | Out-Null

Write-Host "Corridometro OFICIAL $tag" -ForegroundColor Cyan

& (Join-Path $root "build-apk-flavor.ps1") standard conceptA | Out-Null
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$apkSrc = Join-Path $root "app\build\outputs\apk\standard\conceptA\debug\app-standard-conceptA-debug.apk"
$apkDest = Join-Path $oficialDir "corridometro-$tag-oficial-debug.apk"
Copy-Item -Force $apkSrc $apkDest
Write-Host "APK: $apkDest" -ForegroundColor Green

$props = Join-Path $root "keystore.properties"
if (Test-Path $props) {
    & (Join-Path $root "build-play-bundle.ps1")
    $aabName = "corridometro-$tag-playstore.aab"
    $aabFromPlay = Join-Path $playDir $aabName
    if (Test-Path $aabFromPlay) {
        Copy-Item -Force $aabFromPlay (Join-Path $oficialDir $aabName)
        Write-Host "AAB (oficial + playstore): $aabName" -ForegroundColor Green
    }
} else {
    Write-Host ""
    Write-Host 'AVISO: keystore.properties ausente - AAB de release nao gerado.' -ForegroundColor Yellow
    Write-Host '  Para Play Console: create-release-keystore.ps1 e rode build-oficial.ps1 novamente'
}

Write-Host ""
Write-Host 'Pronto. Pasta oficial: releases/oficial/' -ForegroundColor Green
