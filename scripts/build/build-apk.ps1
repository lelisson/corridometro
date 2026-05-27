# Gera APK debug oficial (standard + conceptA).
# Uso: .\scripts\build\build-apk.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
Set-Location $root
Set-CorridometroJavaHome

Write-Host "Corridometro — gerando APK debug (oficial)..." -ForegroundColor Cyan

if (-not (Test-Path ".\gradlew.bat")) {
    Write-Error "Gradle Wrapper ausente. Abra o projeto no Android Studio e sincronize."
}

& .\gradlew.bat assembleStandardConceptADebug
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$apk = Join-Path $root "app\build\outputs\apk\standardConceptA\debug\app-standard-conceptA-debug.apk"
if (-not (Test-Path $apk)) {
    $apk = Get-ChildItem -Path (Join-Path $root "app\build\outputs\apk") -Recurse -Filter "*conceptA*debug*.apk" -ErrorAction SilentlyContinue |
        Select-Object -First 1 -ExpandProperty FullName
}
if (-not (Test-Path $apk)) {
    Write-Error "APK nao encontrado apos o build."
}

$info = Get-Item $apk
$mb = [math]::Round($info.Length / 1MB, 2)
Write-Host ""
Write-Host "BUILD OK" -ForegroundColor Green
Write-Host "Arquivo: $($info.FullName)"
Write-Host "Tamanho: $mb MB"
