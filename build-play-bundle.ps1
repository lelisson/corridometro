# Gera o Android App Bundle (.aab) oficial para Play Store — standard + conceptA
# Uso: .\build-play-bundle.ps1

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
Set-Location $root

$props = Join-Path $root "keystore.properties"
if (-not (Test-Path $props)) {
    Write-Host "FALTA: keystore.properties" -ForegroundColor Red
    Write-Host "  1) Rode: .\create-release-keystore.ps1"
    Write-Host "  2) Ou copie keystore.properties.example para keystore.properties"
    exit 1
}

$studioJbr = "C:\Program Files\Android\Android Studio\jbr"
if (Test-Path "$studioJbr\bin\java.exe") {
    $env:JAVA_HOME = $studioJbr
}

function Get-AppVersion {
    $gradle = Get-Content (Join-Path $root "app\build.gradle.kts") -Raw
    if ($gradle -notmatch '(?s)defaultConfig\s*\{[^}]*versionCode\s*=\s*(\d+)[^}]*versionName\s*=\s*"([^"]+)"') {
        Write-Error "Nao foi possivel ler versionCode/versionName de app/build.gradle.kts"
    }
    return @{ Code = $matches[1]; Name = $matches[2] }
}

$ver = Get-AppVersion
$tag = "v$($ver.Name)-$($ver.Code)"

Write-Host "Gerando bundleStandardConceptARelease (Play Store) $tag ..." -ForegroundColor Cyan
& .\gradlew.bat bundleStandardConceptARelease
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$aab = Join-Path $root "app\build\outputs\bundle\standardConceptARelease\app-standard-conceptA-release.aab"
if (-not (Test-Path $aab)) {
    Write-Error "AAB nao encontrado: $aab"
}

$destDir = Join-Path $root "releases\playstore"
New-Item -ItemType Directory -Force -Path $destDir | Out-Null
$dest = Join-Path $destDir "corridometro-$tag-playstore.aab"
Copy-Item -Force $aab $dest

$mb = [math]::Round((Get-Item $dest).Length / 1MB, 2)
Write-Host ""
Write-Host "PRONTO para upload na Play Console:" -ForegroundColor Green
Write-Host "  $dest"
Write-Host "  Tamanho: $mb MB"
Write-Host ""
Write-Host "Play Console > Seu app > Teste e lancamento > Teste interno > Criar versao > Enviar este .aab"
Write-Host "Guia completo: PLAY_STORE_PUBLISH.txt"
