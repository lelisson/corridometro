# Instala e abre o app no emulador ou celular USB (visual interativo)
# Uso: .\preview-app.ps1
#      .\preview-app.ps1 -Flavor login

param(
    [ValidateSet("standard", "login")]
    [string]$Flavor = "standard"
)

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
Set-Location $ProjectRoot

$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adb)) {
    Write-Error "adb nao encontrado. Instale o Android SDK Platform-Tools."
}

$studioJbr = "C:\Program Files\Android\Android Studio\jbr"
if (Test-Path "$studioJbr\bin\java.exe") {
    $env:JAVA_HOME = $studioJbr
}

$task = "assemble$($Flavor.Substring(0,1).ToUpper() + $Flavor.Substring(1))Debug"
Write-Host "Compilando APK debug ($Flavor)..." -ForegroundColor Cyan
& .\gradlew.bat $task
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$apk = Join-Path $ProjectRoot "app\build\outputs\apk\$Flavor\debug\app-$Flavor-debug.apk"
if (-not (Test-Path $apk)) {
    Write-Error "APK nao encontrado: $apk"
}

$devices = & $adb devices | Select-String "device$"
if (-not $devices) {
    Write-Host ""
    Write-Host "Nenhum dispositivo conectado." -ForegroundColor Yellow
    Write-Host "1) Abra o Android Studio > Device Manager > inicie o emulador Pixel"
    Write-Host "   OU conecte o celular com Depuracao USB"
    Write-Host "2) Rode este script de novo: .\preview-app.ps1"
    Write-Host ""
    Write-Host "Alternativa rapida (sem emulador):" -ForegroundColor Cyan
    Write-Host "  Android Studio > abra ScreenPreviews.kt > aba Split > Preview"
    exit 1
}

Write-Host "Instalando no dispositivo..." -ForegroundColor Cyan
& $adb install -r $apk
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$package = if ($Flavor -eq "login") { "com.corridometro.login" } else { "com.corridometro" }
Write-Host "Abrindo Corridometro ($package)..." -ForegroundColor Cyan
& $adb shell am start -n "$package/.MainActivity"

Write-Host ""
Write-Host "App aberto. Navegue nas abas para ver o visual." -ForegroundColor Green
Write-Host "APK: $apk"
