# Gera .aab assinado para Play Store (standard + conceptA).
# Uso: .\scripts\build\build-play-bundle.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
Set-Location $root
Set-CorridometroJavaHome

$props = Join-Path $root "keystore.properties"
if (-not (Test-Path $props)) {
    Write-Host "FALTA: keystore.properties" -ForegroundColor Red
    Write-Host "  1) Rode: .\scripts\release\create-release-keystore.ps1"
    Write-Host "  2) Ou copie config\keystore.properties.example para keystore.properties"
    exit 1
}

$ver = Get-AppVersionFromGradle -Root $root
$tag = "v$($ver.Name)-$($ver.Code)"

Write-Host "Gerando bundleStandardConceptARelease $tag ..." -ForegroundColor Cyan
& .\gradlew.bat bundleStandardConceptARelease
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$aab = Join-Path $root "app\build\outputs\bundle\standardConceptARelease\app-standard-conceptA-release.aab"
if (-not (Test-Path $aab)) {
    Write-Error "AAB nao encontrado: $aab"
}

$destDir = Join-Path $root "releases\playstore"
New-Item -ItemType Directory -Force -Path $destDir | Out-Null
$dest = Join-Path $destDir "corridometro-$tag-playstore.aab"
Copy-Item -Force $aab $dest

$mb = [math]::Round((Get-Item $dest).Length / 1MB, 2)
Write-Host ""
Write-Host "PRONTO para Play Console:" -ForegroundColor Green
Write-Host "  $dest"
Write-Host "  Tamanho: $mb MB"
Write-Host ""
Write-Host "Guia: docs/guides/publishing/play-store.md"
