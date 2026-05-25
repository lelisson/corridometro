# Valida google-services.json para Corridometro (standard e login)
# Uso: .\check-firebase-setup.ps1

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
$jsonPath = Join-Path $root "app\google-services.json"

Write-Host "Corridometro - verificacao Firebase" -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path $jsonPath)) {
    Write-Host "FALTA: app\google-services.json" -ForegroundColor Red
    Write-Host ""
    Write-Host "Siga FIREBASE_SETUP_LOGIN.txt (versao login) ou FIREBASE_SETUP.txt (padrao)."
    Write-Host "Modelo: app\google-services.json.example"
    exit 1
}

$json = Get-Content $jsonPath -Raw | ConvertFrom-Json
$packages = @()
foreach ($client in $json.client) {
    $pkg = $client.client_info.android_client_info.package_name
    $packages += $pkg
    $webClient = $client.oauth_client | Where-Object { $_.client_type -eq 3 } | Select-Object -First 1
    $hasWeb = $null -ne $webClient -and $webClient.client_id -notmatch "SEU_|SUBSTITUA|000000000000"
    Write-Host "App: $pkg" -ForegroundColor White
    if ($hasWeb) {
        Write-Host "  Web Client ID (OAuth): OK" -ForegroundColor Green
    } else {
        Write-Host "  Web Client ID (OAuth): AUSENTE ou placeholder" -ForegroundColor Yellow
    }
}

$needLogin = "com.corridometro.login"
$needStandard = "com.corridometro"
$hasLogin = $packages -contains $needLogin
$hasStandard = $packages -contains $needStandard

Write-Host ""
if ($hasLogin) {
    Write-Host "Versao LOGIN ($needLogin): OK" -ForegroundColor Green
} else {
    Write-Host "Versao LOGIN ($needLogin): NAO encontrada no JSON" -ForegroundColor Red
    Write-Host "  Adicione outro app Android no Firebase com esse pacote e baixe o JSON de novo."
}

if ($hasStandard) {
    Write-Host "Versao padrao ($needStandard): OK" -ForegroundColor Green
} else {
    Write-Host "Versao padrao ($needStandard): nao no JSON (opcional se so usar login)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Proximos passos no Console Firebase:" -ForegroundColor Cyan
Write-Host "  1. Authentication > Google > Ativar"
Write-Host "  2. Firestore > Criar banco"
Write-Host "  3. Firestore > Regras (copiar firestore.rules)"
Write-Host "  4. Project Settings > SHA-1 do APK (gradlew signingReport)"
Write-Host ""
Write-Host "Depois: .\build-apk-flavor.ps1 login" -ForegroundColor Cyan

if (-not $hasLogin) { exit 1 }
exit 0
