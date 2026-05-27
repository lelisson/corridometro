# APK debug em releases/oficial/ + AAB para Play (standard + conceptA).
# Uso: .\scripts\build\build-oficial.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
Set-Location $root

$ver = Get-AppVersionFromGradle -Root $root
$tag = "v$($ver.Name)-$($ver.Code)"
$oficialDir = Join-Path $root "releases\oficial"
$playDir = Join-Path $root "releases\playstore"
New-Item -ItemType Directory -Force -Path $oficialDir, $playDir | Out-Null

Write-Host "Corridometro OFICIAL $tag" -ForegroundColor Cyan

& (Join-Path $PSScriptRoot "build-apk-flavor.ps1") standard conceptA | Out-Null
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$apkSrc = Join-Path $root "app\build\outputs\apk\standardConceptA\debug\app-standard-conceptA-debug.apk"
$apkDest = Join-Path $oficialDir "corridometro-$tag-oficial-debug.apk"
Copy-Item -Force $apkSrc $apkDest
Write-Host "APK: $apkDest" -ForegroundColor Green

$props = Join-Path $root "keystore.properties"
if (Test-Path $props) {
    & (Join-Path $PSScriptRoot "build-play-bundle.ps1")
    $aabName = "corridometro-$tag-playstore.aab"
    $aabFromPlay = Join-Path $playDir $aabName
    if (Test-Path $aabFromPlay) {
        Copy-Item -Force $aabFromPlay (Join-Path $oficialDir $aabName)
        Write-Host "AAB: $aabName" -ForegroundColor Green
    }
} else {
    Write-Host ""
    Write-Host "AVISO: keystore.properties ausente — AAB nao gerado." -ForegroundColor Yellow
    Write-Host "  Rode: .\scripts\release\create-release-keystore.ps1"
}

Write-Host ""
Write-Host "Pasta: releases\oficial\" -ForegroundColor Green
