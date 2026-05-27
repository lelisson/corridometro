# Emulador em modo estavel (GPU software, sem snapshot).
# Uso: .\scripts\dev\start-emulator-safe.ps1

param([string]$AvdName = "")

$ErrorActionPreference = "Stop"
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$emulator = Join-Path $sdk "emulator\emulator.exe"
$adb = Join-Path $sdk "platform-tools\adb.exe"

if (-not (Test-Path $emulator)) { Write-Error "Emulador nao encontrado." }

if (-not $AvdName) {
    $list = @(& $emulator -list-avds 2>$null)
    if ($list.Count -eq 0) { Write-Host "Crie um AVD no Device Manager." -ForegroundColor Red; exit 1 }
    $AvdName = $list[0].Trim()
}

Write-Host "AVD: $AvdName (modo seguro)" -ForegroundColor Cyan
$emuArgs = @("-avd", $AvdName, "-no-snapshot-load", "-gpu", "swiftshader_indirect", "-memory", "2048")
Start-Process -FilePath $emulator -ArgumentList $emuArgs -WindowStyle Normal

$deadline = (Get-Date).AddMinutes(10)
while ((Get-Date) -lt $deadline) {
    Start-Sleep -Seconds 5
    if (& $adb devices 2>$null | Select-String "emulator-\d+\s+device") {
        Write-Host "Emulador ONLINE." -ForegroundColor Green
        exit 0
    }
}

Write-Host "Ainda offline. Veja docs/guides/development/emulator.md" -ForegroundColor Yellow
