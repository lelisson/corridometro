# Instala e abre o app no emulador ou celular USB.
# Uso: .\scripts\dev\preview-app.ps1
#      .\scripts\dev\preview-app.ps1 -Flavor login

param(
    [ValidateSet("standard", "login")]
    [string]$Flavor = "standard"
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
Set-Location $root
Set-CorridometroJavaHome

$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adb)) {
    Write-Error "adb nao encontrado. Instale Android SDK Platform-Tools."
}

$edition = $Flavor.Substring(0, 1).ToUpper() + $Flavor.Substring(1)
$task = "assemble${edition}ConceptADebug"
Write-Host "Compilando ($Flavor / conceptA)..." -ForegroundColor Cyan
& .\gradlew.bat $task
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$apk = Join-Path $root "app\build\outputs\apk\${Flavor}ConceptA\debug\app-${Flavor}-conceptA-debug.apk"
if (-not (Test-Path $apk)) {
    $apk = Get-ChildItem (Join-Path $root "app\build\outputs\apk") -Recurse -Filter "*${Flavor}*conceptA*debug*.apk" |
        Select-Object -First 1 -ExpandProperty FullName
}
if (-not (Test-Path $apk)) {
    Write-Error "APK nao encontrado apos o build."
}

$devices = & $adb devices | Select-String "device$"
if (-not $devices) {
    Write-Host ""
    Write-Host "Nenhum dispositivo conectado." -ForegroundColor Yellow
    Write-Host "  Emulador: .\scripts\dev\start-emulator.ps1"
    Write-Host "  Ou Preview: docs/guides/development/preview.md"
    exit 1
}

Write-Host "Instalando..." -ForegroundColor Cyan
& $adb install -r $apk
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$package = if ($Flavor -eq "login") { "com.corridometro.login" } else { "com.corridometro" }
& $adb shell am start -n "$package/.MainActivity"
Write-Host "App aberto ($package)." -ForegroundColor Green
