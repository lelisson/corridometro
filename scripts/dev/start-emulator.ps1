# Inicia o emulador Android e aguarda ficar online.
# Uso: .\scripts\dev\start-emulator.ps1

param([string]$AvdName = "")

$ErrorActionPreference = "Stop"
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$emulator = Join-Path $sdk "emulator\emulator.exe"
$adb = Join-Path $sdk "platform-tools\adb.exe"

if (-not (Test-Path $emulator)) {
    Write-Error "Emulador nao encontrado. Instale via Android Studio > SDK Manager."
}

if (-not $AvdName) {
    $list = & $emulator -list-avds 2>$null
    if (-not $list) {
        Write-Host "Crie um AVD: Android Studio > Device Manager > Create Device"
        exit 1
    }
    $AvdName = ($list | Select-Object -First 1).Trim()
    Write-Host "AVD: $AvdName" -ForegroundColor Cyan
}

if (& $adb devices 2>$null | Select-String "emulator-\d+\s+device") {
    Write-Host "Emulador ja online." -ForegroundColor Green
    exit 0
}

Write-Host "Iniciando emulador..." -ForegroundColor Cyan
Start-Process -FilePath $emulator -ArgumentList @("-avd", $AvdName) -WindowStyle Normal

$deadline = (Get-Date).AddMinutes(8)
while ((Get-Date) -lt $deadline) {
    Start-Sleep -Seconds 4
    if (& $adb devices 2>$null | Select-String "emulator-\d+\s+device") {
        Write-Host "Emulador ONLINE. Rode Run no Android Studio." -ForegroundColor Green
        exit 0
    }
    Write-Host "." -NoNewline
}

Write-Error "Timeout. Veja docs/guides/development/emulator.md"
