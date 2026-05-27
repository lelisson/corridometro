# Gera APKs de design A e B para comparar no celular.
# Uso: .\scripts\build\build-design-apks.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\..\lib\Common.ps1"
$root = Get-CorridometroRoot
Set-Location $root
Set-CorridometroJavaHome

Write-Host "Corridometro — APKs design A e B..." -ForegroundColor Cyan
& .\gradlew.bat assembleStandardConceptADebug assembleStandardConceptBDebug
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$outDir = Join-Path $root "app\designs\output"
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

$variants = @(
    @{ Name = "a"; Path = "app\build\outputs\apk\standardConceptA\debug\app-standard-conceptA-debug.apk" },
    @{ Name = "b"; Path = "app\build\outputs\apk\standardConceptB\debug\app-standard-conceptB-debug.apk" }
)

foreach ($v in $variants) {
    $src = Join-Path $root $v.Path
    if (-not (Test-Path $src)) {
        Write-Error "APK $($v.Name) nao encontrado."
    }
    $dest = Join-Path $outDir "corridometro-design-$($v.Name)-debug.apk"
    Copy-Item -Path $src -Destination $dest -Force
    $mb = [math]::Round((Get-Item $dest).Length / 1MB, 2)
    Write-Host "OK  $dest  ($mb MB)" -ForegroundColor Green
}

Write-Host ""
Write-Host "  A — com.corridometro (oficial)"
Write-Host "  B — com.corridometro.design.b (mockup)"
Write-Host "Pasta: $outDir"
