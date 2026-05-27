# Cria keystore de release (uma vez). Guarde as senhas em local seguro.
# Uso: .\scripts\release\create-release-keystore.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
Set-Location $root
Set-CorridometroJavaHome

$keystoreDir = Join-Path $root "keystore"
$jks = Join-Path $keystoreDir "corridometro-release.jks"
$props = Join-Path $root "keystore.properties"

if (Test-Path $jks) {
    Write-Host "Ja existe: $jks" -ForegroundColor Yellow
    Write-Host "Se perdeu a senha, nao da para publicar updates com a mesma chave na Play."
    exit 1
}

$keytool = "$env:JAVA_HOME\bin\keytool.exe"
if (-not (Test-Path $keytool)) {
    Write-Error "keytool nao encontrado."
}

New-Item -ItemType Directory -Force -Path $keystoreDir | Out-Null

Write-Host ""
Write-Host "Corridometro — keystore de RELEASE" -ForegroundColor Cyan
Write-Host "Guarde as senhas! Sem elas voce NAO atualiza o app na Play Store." -ForegroundColor Yellow
Write-Host ""

$storePass = Read-Host "Senha da keystore (min. 6 caracteres)" -AsSecureString
$storePassPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($storePass))
$keyPassPlain = Read-Host "Senha da chave (Enter = mesma da keystore)"
if ([string]::IsNullOrWhiteSpace($keyPassPlain)) { $keyPassPlain = $storePassPlain }

$dname = Read-Host "Nome (CN) [Corridometro]"
if ([string]::IsNullOrWhiteSpace($dname)) { $dname = "Corridometro" }

& $keytool -genkeypair -v `
    -keystore $jks `
    -alias corridometro `
    -keyalg RSA -keysize 2048 -validity 10000 `
    -storepass $storePassPlain `
    -keypass $keyPassPlain `
    -dname "CN=$dname, OU=Dev, O=Corridometro, L=Brasil, C=BR"

@"
storeFile=keystore/corridometro-release.jks
storePassword=$storePassPlain
keyPassword=$keyPassPlain
keyAlias=corridometro
"@ | Set-Content -Path $props -Encoding ASCII

Write-Host ""
Write-Host "OK: $jks" -ForegroundColor Green
Write-Host "OK: $props (nao commite no Git)" -ForegroundColor Green
Write-Host ""
Write-Host "Proximo: .\scripts\build\build-play-bundle.ps1" -ForegroundColor Cyan
