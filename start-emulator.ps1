# Inicia o emulador e espera ficar online (rode ANTES do Run no Android Studio)
# Uso: .\start-emulator.ps1
#      .\start-emulator.ps1 -AvdName "Pixel_6_API_34"

param(
    [string]$AvdName = ""
)

$ErrorActionPreference = "Stop"
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$emulator = Join-Path $sdk "emulator\emulator.exe"
$adb = Join-Path $sdk "platform-tools\adb.exe"

if (-not (Test-Path $emulator)) {
    Write-Error "Emulador nao encontrado. Instale via Android Studio > SDK Manager > Android Emulator."
}

if (-not $AvdName) {
    $list = & $emulator -list-avds 2>$null
    if (-not $list) {
        Write-Host "Nenhum AVD criado." -ForegroundColor Red
        Write-Host "Android Studio > Tools > Device Manager > Create Device (Pixel 6, API 34, Google Play)"
        exit 1
    }
    $AvdName = ($list | Select-Object -First 1).Trim()
    Write-Host "AVD: $AvdName" -ForegroundColor Cyan
}

$running = & $adb devices 2>$null | Select-String "emulator-\d+\s+device"
if ($running) {
    Write-Host "Emulador ja esta online." -ForegroundColor Green
    & $adb devices
    exit 0
}

Write-Host "Iniciando emulador (pode levar 1-3 min na primeira vez)..." -ForegroundColor Cyan
Start-Process -FilePath $emulator -ArgumentList @("-avd", $AvdName) -WindowStyle Normal

$deadline = (Get-Date).AddMinutes(8)
while ((Get-Date) -lt $deadline) {
    Start-Sleep -Seconds 4
    $online = & $adb devices 2>$null | Select-String "emulator-\d+\s+device"
    if ($online) {
        Write-Host ""
        Write-Host "Emulador ONLINE. Agora no Android Studio: Run > Corridometro (oficial)" -ForegroundColor Green
        & $adb devices
        exit 0
    }
    Write-Host "." -NoNewline
}

Write-Host ""
Write-Error "Timeout: emulador nao respondeu em 8 min. Veja EMULADOR_ANDROID_STUDIO.txt"
