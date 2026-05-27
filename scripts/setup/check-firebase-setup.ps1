# Valida app/google-services.json (pacotes e OAuth).
# Uso: .\scripts\setup\check-firebase-setup.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
$jsonPath = Join-Path $root "app\google-services.json"

Write-Host "Corridometro — verificacao Firebase" -ForegroundColor Cyan

if (-not (Test-Path $jsonPath)) {
    Write-Host "FALTA: app\google-services.json" -ForegroundColor Red
    Write-Host "Guia: docs/guides/integrations/firebase.md"
    exit 1
}

$json = Get-Content $jsonPath -Raw | ConvertFrom-Json
$packages = @()
foreach ($client in $json.client) {
    $pkg = $client.client_info.android_client_info.package_name
    $packages += $pkg
    $webClient = $client.oauth_client | Where-Object { $_.client_type -eq 3 } | Select-Object -First 1
    $hasWeb = $null -ne $webClient -and $webClient.client_id -notmatch "SEU_|SUBSTITUA|000000000000"
    Write-Host "App: $pkg"
    if ($hasWeb) { Write-Host "  OAuth Web Client: OK" -ForegroundColor Green }
    else { Write-Host "  OAuth Web Client: ausente ou placeholder" -ForegroundColor Yellow }
}

$needLogin = "com.corridometro.login"
$needStandard = "com.corridometro"
Write-Host ""
if ($packages -contains $needLogin) { Write-Host "Login ($needLogin): OK" -ForegroundColor Green }
else { Write-Host "Login ($needLogin): NAO no JSON" -ForegroundColor Red }

if ($packages -contains $needStandard) { Write-Host "Oficial ($needStandard): OK" -ForegroundColor Green }
else { Write-Host "Oficial ($needStandard): opcional se so usar login" -ForegroundColor Yellow }

Write-Host ""
Write-Host "Regras Firestore: firebase/firestore.rules"
Write-Host "Guia: docs/guides/integrations/firebase-login.md"

if ($packages -notcontains $needLogin -and $packages -notcontains $needStandard) { exit 1 }
