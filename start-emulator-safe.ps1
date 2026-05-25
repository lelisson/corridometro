# Inicia emulador com opcoes estaveis (se Pixel_7 "terminated", use este)
# Uso: .\start-emulator-safe.ps1
#      .\start-emulator-safe.ps1 -AvdName "Pixel_6_API_34"

param(
    [string]$AvdName = ""
)

$ErrorActionPreference = "Stop"
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$emulator = Join-Path $sdk "emulator\emulator.exe"
$adb = Join-Path $sdk "platform-tools\adb.exe"

if (-not (Test-Path $emulator)) {
    Write-Error "Emulador nao encontrado. SDK: $sdk"
}

if (-not $AvdName) {
    $list = @(& $emulator -list-avds 2>$null)
    if ($list.Count -eq 0) {
        Write-Host "Crie um AVD: Android Studio > Device Manager > Create Device" -ForegroundColor Red
        exit 1
    }
    $AvdName = $list[0].Trim()
}

Write-Host "AVD: $AvdName" -ForegroundColor Cyan
Write-Host "Modo seguro: sem snapshot, GPU software (mais lento, mais estavel)" -ForegroundColor Yellow

$args = @(
    "-avd", $AvdName,
    "-no-snapshot-load",
    "-gpu", "swiftshader_indirect",
    "-memory", "2048"
)

Start-Process -FilePath $emulator -ArgumentList $args -WindowStyle Normal

$deadline = (Get-Date).AddMinutes(10)
while ((Get-Date) -lt $deadline) {
    Start-Sleep -Seconds 5
    $online = & $adb devices 2>$null | Select-String "emulator-\d+\s+device"
    if ($online) {
        Write-Host "Emulador ONLINE." -ForegroundColor Green
        & $adb devices
        exit 0
    }
}

Write-Host "Ainda nao online. Veja a janela do emulador por mensagem de erro." -ForegroundColor Yellow
Write-Host "Guia: EMULADOR_ANDROID_STUDIO.txt"
